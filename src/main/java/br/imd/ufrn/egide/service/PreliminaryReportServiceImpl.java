package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.PreliminaryReportRequestDTO;
import br.imd.ufrn.egide.dto.PreliminaryReportResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionRequestDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;
import br.imd.ufrn.egide.entity.PreliminaryReportEntity;
import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.ReportProcessedEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.PreliminaryReportDecision;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.PreliminaryReportRepository;
import br.imd.ufrn.egide.repository.ReportAiAnalysedRepository;
import br.imd.ufrn.egide.repository.ReportProcessedRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

// Fase 2 — gerencia emissão e consulta do parecer preliminar pelo Ouvidor designado ao caso.
@Service
@RequiredArgsConstructor
public class PreliminaryReportServiceImpl implements PreliminaryReportService {

    private final ReportService reportService;
    private final ReportAiService reportAiService;
    private final ReportAiAnalysedRepository reportAiAnalysedRepository;
    private final ReportProcessedRepository reportProcessedRepository;
    private final PreliminaryReportRepository preliminaryReportRepository;
    private final UserInfoRepository userInfoRepository;
    private final NotificationService notificationService;

    // Chama o microserviço de IA para obter sugestão de resposta usando dados anonimizados quando disponíveis.
    @Override
    public ReportResponseSuggestionResponseDTO suggestResponse(Long reportId) {
        ReportEntity report = reportService.findEntityById(reportId);
        ReportAiAnalysedEntity ai = reportAiAnalysedRepository.findByReportId(reportId).orElse(null);

        String title = ai != null && ai.getTitleAnonymized() != null ? ai.getTitleAnonymized() : report.getTitle();
        String description = ai != null && ai.getDescriptionAnonymized() != null ? ai.getDescriptionAnonymized() : report.getDescription();
        String category = ai != null && ai.getCategory() != null ? ai.getCategory().name() : null;
        String risk = ai != null && ai.getRisk() != null ? ai.getRisk().name() : null;

        return reportAiService.suggestResponse(
                new ReportResponseSuggestionRequestDTO(
                        report.getId(),
                        title,
                        description,
                        report.getProtocolNumber(),
                        category,
                        risk
                )
        );
    }

    @Override
    @Transactional
    public PreliminaryReportResponseDTO submit(Long reportId, PreliminaryReportRequestDTO request) {
        if (request == null || request.decision() == null) {
            throw new BusinessException("Decisão do parecer é obrigatória.", HttpStatus.BAD_REQUEST);
        }

        validateBusinessRules(request);

        ReportEntity report = reportService.findEntityById(reportId);
        UserInfoEntity ouvidor = requireOuvidor();
        ensureAssignedOuvidor(report, ouvidor);

        PreliminaryReportEntity entity = preliminaryReportRepository.findByReportId(reportId)
                .orElseGet(PreliminaryReportEntity::new);
        entity.setReport(report);
        entity.setOuvidor(ouvidor);
        entity.setDecision(request.decision());
        entity.setJustification(trim(request.justification()));

        if (request.decision() == PreliminaryReportDecision.ACATAR) {
            entity.setPenaltyType(request.penaltyType());
            entity.setPenaltyDescription(trim(request.penaltyDescription()));
        } else {
            entity.setPenaltyType(null);
            entity.setPenaltyDescription(null);
        }

        String aiSuggestion = trim(request.aiSuggestion());
        entity.setAiSuggestion(aiSuggestion);
        entity.setUsedAiSuggestion(aiSuggestion != null && matchesSuggestion(entity, aiSuggestion));
        entity.setSubmittedAt(LocalDateTime.now());
        entity = preliminaryReportRepository.save(entity);

        if (report.getUserInfo() != null) {
            notificationService.notifyDenunciantePreliminaryIssued(report.getId(), report.getUserInfo().getId());
        }

        ReportStatus newStatus;
        if (request.decision() == PreliminaryReportDecision.NEGAR_FALTA_PROVAS) {
            newStatus = ReportStatus.CLOSED_NO_PROOFS;
            report.setStatus(newStatus);
            report.setDenouncedUser(null);
            report.setPhase3NotifiedAt(null);
        } else {
            UserInfoEntity denounced = userInfoRepository.findById(request.denouncedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário denunciado não encontrado"));

            newStatus = ReportStatus.DEFENSE_OPEN;
            report.setStatus(newStatus);
            report.setDenouncedUser(denounced);

            if (report.getPhase3NotifiedAt() == null) {
                notificationService.notifyDenouncedPhase3Started(report.getId(), denounced.getId());
                report.setPhase3NotifiedAt(LocalDateTime.now());
            }
        }

        ReportProcessedEntity processed = reportProcessedRepository.findByReportId(reportId).orElse(null);
        if (processed != null) {
            processed.setStatus(newStatus);
            reportProcessedRepository.save(processed);
        }

        return toDTO(entity, report);
    }

    // Recupera o parecer preliminar existente ou lança 404 se ainda não foi emitido.
    @Override
    public PreliminaryReportResponseDTO getByReportId(Long reportId) {
        PreliminaryReportEntity entity = preliminaryReportRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Parecer preliminar não encontrado"));
        return toDTO(entity, entity.getReport());
    }

    // ACATAR exige penalidade; NEGAR e NEGAR_FALTA_PROVAS exigem justificativa.
    private void validateBusinessRules(PreliminaryReportRequestDTO request) {
        switch (request.decision()) {
            case ACATAR -> {
                if (request.penaltyType() == null) {
                    throw new BusinessException(
                            "Penalidade é obrigatória ao acatar a denúncia.",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
            case NEGAR, NEGAR_FALTA_PROVAS -> {
                if (trim(request.justification()) == null) {
                    throw new BusinessException(
                            "Justificativa é obrigatória ao negar a denúncia.",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
        }
        if (request.decision() != PreliminaryReportDecision.NEGAR_FALTA_PROVAS && request.denouncedUserId() == null) {
            throw new BusinessException(
                    "O usuário denunciado é obrigatório para avançar o caso para a defesa do denunciado.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // Retorna o usuário autenticado garantindo que é LISTENER ou ADMIN.
    private UserInfoEntity requireOuvidor() {
        UserInfoEntity user = currentUser();
        if (user.getRole() != Role.LISTENER && user.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Apenas Ouvidores podem emitir parecer preliminar.",
                    HttpStatus.FORBIDDEN
            );
        }
        return user;
    }

    // Impede que um ouvidor diferente do designado edite o caso.
    private void ensureAssignedOuvidor(ReportEntity report, UserInfoEntity ouvidor) {
        if (ouvidor.getRole() == Role.ADMIN) {
            return; // Admin pode operar em nome de qualquer ouvidor.
        }
        if (report.getOuvidor() == null || !Objects.equals(report.getOuvidor().getId(), ouvidor.getId())) {
            throw new BusinessException(
                    "Você não é o Ouvidor designado para este caso.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    // Verifica se o texto do parecer foi usado sem alteração da sugestão da IA (flag usedAiSuggestion).
    private boolean matchesSuggestion(PreliminaryReportEntity entity, String aiSuggestion) {
        String candidate = entity.getJustification();
        if (candidate == null) {
            candidate = entity.getPenaltyDescription();
        }
        if (candidate == null) {
            return false;
        }
        return normalize(candidate).equals(normalize(aiSuggestion));
    }

    // Colapsa espaços múltiplos para comparação normalizada de strings.
    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    // Retorna null para strings vazias, evitando persistir valores sem conteúdo.
    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    // Converte entidade + report para o DTO de resposta ao cliente.
    private PreliminaryReportResponseDTO toDTO(PreliminaryReportEntity entity, ReportEntity report) {
        return new PreliminaryReportResponseDTO(
                entity.getId(),
                report.getId(),
                entity.getOuvidor() != null ? entity.getOuvidor().getId() : null,
                entity.getOuvidor() != null ? entity.getOuvidor().getName() : null,
                entity.getDecision(),
                entity.getJustification(),
                entity.getPenaltyType(),
                entity.getPenaltyDescription(),
                entity.getAiSuggestion(),
                entity.getUsedAiSuggestion(),
                report.getStatus() != null ? report.getStatus().name() : null,
                entity.getSubmittedAt()
        );
    }

    private UserInfoEntity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }
}
