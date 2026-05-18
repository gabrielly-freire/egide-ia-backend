package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.AppealRequestDTO;
import br.imd.ufrn.egide.dto.AppealResponseDTO;
import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorCaseDTO;
import br.imd.ufrn.egide.entity.AppealEntity;
import br.imd.ufrn.egide.entity.AppealReportEntity;
import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.AppealStatus;
import br.imd.ufrn.egide.enums.AppellantRole;
import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.AppealReportRepository;
import br.imd.ufrn.egide.repository.AppealRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Implementação da Fase 5: recursos submetidos pelas partes após validação da OG.
// Responsabilidades:
//   - Receber recursos de denunciante ou denunciado (1 por parte por caso).
//   - Designar novo ouvidor via sorteio com anti-viés; aplicar regra de merge quando ambas as partes recorrem.
//   - Listar recursos de uma manifestação e casos atribuídos ao novo ouvidor.
//   - Permitir que o novo ouvidor emita o AppealReport consolidado.
// Regra de merge: quando já existe um recurso para o caso, o novo recurso herda o mesmo ouvidor
// do primeiro, garantindo que um único ouvidor analise todos os recursos de um mesmo caso.
// Anti-viés: o OuvidorCaseDTO retornado ao novo ouvidor não expõe conclusões anteriores
// (parecer, defesa, relatório final, histórico da OG), protegendo a imparcialidade da análise.
@Service
@RequiredArgsConstructor
// Fase 5 — gerencia submissão, consulta e análise de recursos por denunciante/denunciado e pelo novo ouvidor designado.
public class AppealServiceImpl implements AppealService {

    private final ReportService reportService;
    private final AppealRepository appealRepository;
    private final AppealReportRepository appealReportRepository;
    private final UserInfoRepository userInfoRepository;
    private final OuvidorAssignmentService ouvidorAssignmentService;

    // Abre recurso para a parte autenticada; sorteia novo ouvidor (anti-viés) e aplica regra de merge se ambas as partes recorrem.
    @Override
    @Transactional
    public AppealResponseDTO submit(Long reportId, AppealRequestDTO request) {
        if (request == null || request.grounds() == null || request.grounds().isBlank()) {
            throw new BusinessException("Os fundamentos do recurso são obrigatórios.", HttpStatus.BAD_REQUEST);
        }

        ReportEntity report = reportService.findEntityById(reportId);

        // Recurso só é permitido após validação da OG; bloqueia tentativas prematuras.
        if (report.getStatus() != ReportStatus.GENERAL_VALIDATED) {
            throw new BusinessException(
                    "O recurso só pode ser aberto após a decisão do Ouvidor Geral.",
                    HttpStatus.CONFLICT
            );
        }

        UserInfoEntity appellant = currentUser();
        AppellantRole role = resolveAppellantRole(report, appellant, request.appellantRole());

        // Verifica unicidade por parte: UNIQUE (report_id, appellant_role) no nível de aplicação.
        if (appealRepository.findByReportIdAndAppellantRole(reportId, role).isPresent()) {
            throw new BusinessException(
                    "Esta parte já apresentou recurso para este caso.",
                    HttpStatus.CONFLICT
            );
        }

        List<AppealEntity> existing = appealRepository.findAllByReportId(reportId);
        UserInfoEntity newOuvidor;
        if (!existing.isEmpty() && existing.get(0).getNewOuvidor() != null) {
            // Regra de merge: reutiliza o ouvidor já sorteado no primeiro recurso.
            newOuvidor = existing.get(0).getNewOuvidor();
        } else {
            // Primeiro recurso do caso: sorteia novo ouvidor excluindo os que já participaram.
            newOuvidor = ouvidorAssignmentService.assignOuvidor(collectOuvidorIdsAlreadyOnCase(report));
        }

        AppealEntity entity = new AppealEntity();
        entity.setReport(report);
        entity.setAppellant(appellant);
        entity.setAppellantRole(role);
        entity.setGrounds(request.grounds().trim());
        entity.setNewOuvidor(newOuvidor);
        entity.setStatus(AppealStatus.UNDER_ANALYSIS);
        entity.setSubmittedAt(LocalDateTime.now());
        entity = appealRepository.save(entity);

        if (existing.isEmpty()) {
            // Primeiro recurso: atualiza o status da manifestação para APPEAL_UNDER_ANALYSIS.
            report.setStatus(ReportStatus.APPEAL_UNDER_ANALYSIS);
        } else {
            // Segundo recurso (merge): propaga o ouvidor designado para o primeiro recurso, caso ainda não tenha.
            existing.forEach(a -> {
                if (a.getNewOuvidor() == null) {
                    a.setNewOuvidor(newOuvidor);
                    appealRepository.save(a);
                }
            });
        }

        return toDTO(entity);
    }

    // Lista todos os recursos abertos para uma manifestação.
    @Override
    public List<AppealResponseDTO> listByReport(Long reportId) {
        reportService.findEntityById(reportId); // 404 se não existir
        return appealRepository.findAllByReportId(reportId).stream().map(this::toDTO).toList();
    }

    // Retorna casos onde o ouvidor autenticado foi designado como novo ouvidor de recurso; deduplicação por ID evita StackOverflow do @Data bidirecional.
    @Override
    public List<OuvidorCaseDTO> findAppealCasesAssignedToCurrentOuvidor() {
        UserInfoEntity user = currentUser();
        if (user.getRole() != Role.LISTENER && user.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Apenas Ouvidores podem listar recursos atribuídos.",
                    HttpStatus.FORBIDDEN
            );
        }

        // LinkedHashMap preserva a ordem de inserção; deduplicação por ID evita conflitos
        // de StackOverflow causados pela navegação bidirecional do @Data do Lombok.
        return appealRepository.findAllByNewOuvidorId(user.getId()).stream()
                .map(AppealEntity::getReport)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        r -> r.getId(),
                        r -> r,
                        (a, b) -> a,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(this::toAppealCaseDTO)
                .toList();
    }

    // Novo ouvidor emite relatório consolidado do recurso; avança status para APPEAL_AWAITING_GENERAL (fila da OG).
    @Override
    @Transactional
    public FinalReportResponseDTO submitAppealReport(Long reportId, FinalReportRequestDTO request) {
        if (request == null || request.decision() == null) {
            throw new BusinessException("Decisão do relatório do recurso é obrigatória.", HttpStatus.BAD_REQUEST);
        }
        validate(request);

        ReportEntity report = reportService.findEntityById(reportId);
        UserInfoEntity newOuvidor = currentUser();

        // Verifica se o ouvidor autenticado é de fato o designado para os recursos deste caso.
        List<AppealEntity> appeals = appealRepository.findAllByReportId(reportId);
        boolean isAssigned = appeals.stream().anyMatch(a -> a.getNewOuvidor() != null
                && Objects.equals(a.getNewOuvidor().getId(), newOuvidor.getId()));
        if (!isAssigned && newOuvidor.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Você não é o novo ouvidor designado para o(s) recurso(s) deste caso.",
                    HttpStatus.FORBIDDEN
            );
        }

        // Upsert: reutiliza registro existente se houver (ex.: correção antes da OG avaliar).
        AppealReportEntity entity = appealReportRepository.findByReportId(reportId)
                .orElseGet(AppealReportEntity::new);
        entity.setReport(report);
        entity.setNewOuvidor(newOuvidor);
        entity.setDecision(request.decision());
        entity.setJustification(trim(request.justification()));
        if (request.decision() == FinalReportDecision.ACATAR) {
            entity.setPenaltyType(request.penaltyType());
            entity.setPenaltyDescription(trim(request.penaltyDescription()));
        } else {
            // NEGAR: limpa penalidade para evitar dados inconsistentes no banco.
            entity.setPenaltyType(null);
            entity.setPenaltyDescription(null);
        }
        entity.setSubmittedAt(LocalDateTime.now());
        entity = appealReportRepository.save(entity);

        // Avança todos os recursos do caso para AWAITING_GENERAL sincronicamente.
        appeals.forEach(a -> {
            a.setStatus(AppealStatus.AWAITING_GENERAL);
            appealRepository.save(a);
        });

        report.setStatus(ReportStatus.APPEAL_AWAITING_GENERAL);

        return new FinalReportResponseDTO(
                entity.getId(),
                report.getId(),
                newOuvidor.getId(),
                newOuvidor.getName(),
                null,
                entity.getDecision(),
                entity.getJustification(),
                entity.getPenaltyType(),
                entity.getPenaltyDescription(),
                report.getStatus().name(),
                entity.getSubmittedAt()
        );
    }

    // ACATAR exige penalidade; NEGAR exige justificativa.
    private void validate(FinalReportRequestDTO request) {
        switch (request.decision()) {
            case ACATAR -> {
                if (request.penaltyType() == null) {
                    throw new BusinessException(
                            "Penalidade é obrigatória ao acatar no relatório do recurso.",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
            case NEGAR -> {
                if (trim(request.justification()) == null) {
                    throw new BusinessException(
                            "Justificativa é obrigatória ao negar no relatório do recurso.",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
        }
    }

    // Infere se o usuário é DENUNCIANTE ou DENUNCIADO com base no vínculo com a manifestação.
    private AppellantRole resolveAppellantRole(ReportEntity report,
                                               UserInfoEntity appellant,
                                               AppellantRole hint) {
        boolean isDenunciante = report.getUserInfo() != null
                && Objects.equals(report.getUserInfo().getId(), appellant.getId());
        boolean isDenunciado = report.getDenouncedUser() != null
                && Objects.equals(report.getDenouncedUser().getId(), appellant.getId());

        // Bloqueia usuários sem vínculo com o caso (exceto ADMIN, que pode usar o hint).
        if (!isDenunciante && !isDenunciado && appellant.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Apenas o denunciante ou o denunciado podem recorrer.",
                    HttpStatus.FORBIDDEN
            );
        }

        // ADMIN usa o hint apenas quando não é possível inferir o papel automaticamente.
        AppellantRole inferred = isDenunciante ? AppellantRole.DENUNCIANTE
                                : isDenunciado ? AppellantRole.DENUNCIADO
                                : hint;
        if (inferred == null) {
            throw new BusinessException("Não foi possível inferir o papel do recorrente.", HttpStatus.BAD_REQUEST);
        }
        return inferred;
    }

    // Coleta IDs de ouvidores que já tocaram o caso para garantir anti-viés na designação do novo ouvidor do recurso.
    private List<Long> collectOuvidorIdsAlreadyOnCase(ReportEntity report) {
        List<Long> ids = new ArrayList<>();
        if (report.getOuvidor() != null) {
            ids.add(report.getOuvidor().getId());
        }
        if (report.getPreliminaryReport() != null && report.getPreliminaryReport().getOuvidor() != null) {
            ids.add(report.getPreliminaryReport().getOuvidor().getId());
        }
        if (report.getFinalReport() != null && report.getFinalReport().getOuvidor() != null) {
            ids.add(report.getFinalReport().getOuvidor().getId());
        }
        return ids;
    }

    // Retorna a entidade do usuário autenticado pelo SecurityContext.
    private UserInfoEntity currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }

    // Converte AppealEntity para o DTO de resposta.
    private AppealResponseDTO toDTO(AppealEntity entity) {
        return new AppealResponseDTO(
                entity.getId(),
                entity.getReport() != null ? entity.getReport().getId() : null,
                entity.getAppellant() != null ? entity.getAppellant().getId() : null,
                entity.getAppellant() != null ? entity.getAppellant().getName() : null,
                entity.getAppellantRole(),
                entity.getGrounds(),
                entity.getNewOuvidor() != null ? entity.getNewOuvidor().getId() : null,
                entity.getNewOuvidor() != null ? entity.getNewOuvidor().getName() : null,
                entity.getStatus(),
                entity.getSubmittedAt(),
                entity.getClosedAt()
        );
    }

    // Monta o DTO para o novo ouvidor omitindo conclusões anteriores (parecer, defesa, relatório final) — garantia de anti-viés.
    private OuvidorCaseDTO toAppealCaseDTO(ReportEntity report) {
        ReportAiAnalysedEntity ai = report.getReportAiAnalysed();
        return new OuvidorCaseDTO(
                report.getId(),
                report.getProtocolNumber(),
                report.getTitle(),
                report.getDescription(),
                report.getDateOfOccurrence(),
                report.getStatus() != null ? report.getStatus().name() : null,
                ai != null ? ai.getCategory() : null,
                ai != null ? ai.getRisk() : null,
                // Reutiliza o campo para indicar se o AppealReport já foi submetido.
                appealReportRepository.findByReportId(report.getId()).isPresent(),
                report.getCreatedAt()
        );
    }

    // Retorna null para strings vazias, evitando persistir valores sem conteúdo.
    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
