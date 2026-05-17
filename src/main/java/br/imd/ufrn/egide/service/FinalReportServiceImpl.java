package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;
import br.imd.ufrn.egide.entity.DefenseEntity;
import br.imd.ufrn.egide.entity.FinalReportEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.ReportProcessedEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.DefenseRepository;
import br.imd.ufrn.egide.repository.FinalReportRepository;
import br.imd.ufrn.egide.repository.ReportProcessedRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// Fase 3 conclusão — gerencia emissão e consulta do relatório final pelo Ouvidor, após análise da defesa.
@Service
@RequiredArgsConstructor
public class FinalReportServiceImpl implements FinalReportService {

    private final DefenseRepository defenseRepository;
    private final FinalReportRepository finalReportRepository;
    private final ReportService reportService;
    private final UserInfoRepository userInfoRepository;
    private final ReportProcessedRepository reportProcessedRepository;

    // Valida e persiste o relatório final; avança o status para FINAL_ISSUED (fila de validação da OG).
    @Override
    @Transactional
    public FinalReportResponseDTO submit(Long reportId, FinalReportRequestDTO request) {
        if (request == null || request.decision() == null) {
            throw new BusinessException("Decisão do relatório final é obrigatória.", HttpStatus.BAD_REQUEST);
        }
        validate(request);

        ReportEntity report = reportService.findEntityById(reportId);
        UserInfoEntity ouvidor = requireOuvidor();
        ensureAssignedOuvidor(report, ouvidor);

        DefenseEntity defense = defenseRepository.findByReportId(reportId)
                .orElseThrow(() -> new BusinessException("Defesa ainda não enviada para este caso.", HttpStatus.BAD_REQUEST));
        if (defense.getSubmittedAt() == null) {
            throw new BusinessException("Defesa ainda não enviada para este caso.", HttpStatus.BAD_REQUEST);
        }

        if (finalReportRepository.findByReportId(reportId).isPresent()) {
            throw new BusinessException("Relatório final já emitido para este caso.", HttpStatus.CONFLICT);
        }

        FinalReportEntity entity = new FinalReportEntity();
        entity.setReport(report);
        entity.setOuvidor(ouvidor);
        entity.setDefenseId(request.defenseId() != null ? request.defenseId() : defense.getId());
        entity.setDecision(request.decision());
        entity.setJustification(trim(request.justification()));

        if (request.decision() == FinalReportDecision.ACATAR) {
            entity.setPenaltyType(request.penaltyType());
            entity.setPenaltyDescription(trim(request.penaltyDescription()));
        } else {
            entity.setPenaltyType(null);
            entity.setPenaltyDescription(null);
        }

        entity.setSubmittedAt(LocalDateTime.now());
        entity = finalReportRepository.save(entity);

        report.setStatus(ReportStatus.FINAL_ISSUED);

        ReportProcessedEntity processed = reportProcessedRepository.findByReportId(reportId).orElse(null);
        if (processed != null) {
            processed.setStatus(ReportStatus.FINAL_ISSUED);
            reportProcessedRepository.save(processed);
        }

        return toDTO(entity, report);
    }

    // Recupera o relatório final ou lança 404.
    @Override
    public FinalReportResponseDTO getByReportId(Long reportId) {
        FinalReportEntity entity = finalReportRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Relatório final não encontrado"));
        return toDTO(entity, entity.getReport());
    }

    // ACATAR exige penalidade; NEGAR exige justificativa.
    private void validate(FinalReportRequestDTO request) {
        switch (request.decision()) {
            case ACATAR -> {
                if (request.penaltyType() == null) {
                    throw new BusinessException(
                            "Penalidade é obrigatória ao acatar a denúncia no relatório final.",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
            case NEGAR -> {
                if (trim(request.justification()) == null) {
                    throw new BusinessException(
                            "Justificativa é obrigatória ao negar a denúncia no relatório final.",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
        }
    }

    // Retorna o usuário autenticado garantindo que é LISTENER ou ADMIN.
    private UserInfoEntity requireOuvidor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfoEntity user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
        if (user.getRole() != Role.LISTENER && user.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Apenas Ouvidores podem submeter relatório final.",
                    HttpStatus.FORBIDDEN
            );
        }
        return user;
    }

    // Impede que um ouvidor diferente do designado submeta o relatório final.
    private void ensureAssignedOuvidor(ReportEntity report, UserInfoEntity ouvidor) {
        if (ouvidor.getRole() == Role.ADMIN) {
            return;
        }
        if (report.getOuvidor() == null || !report.getOuvidor().getId().equals(ouvidor.getId())) {
            throw new BusinessException(
                    "Você não é o Ouvidor designado para este caso.",
                    HttpStatus.FORBIDDEN
            );
        }
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
    private FinalReportResponseDTO toDTO(FinalReportEntity entity, ReportEntity report) {
        return new FinalReportResponseDTO(
                entity.getId(),
                report.getId(),
                entity.getOuvidor() != null ? entity.getOuvidor().getId() : null,
                entity.getOuvidor() != null ? entity.getOuvidor().getName() : null,
                entity.getDefenseId(),
                entity.getDecision(),
                entity.getJustification(),
                entity.getPenaltyType(),
                entity.getPenaltyDescription(),
                report.getStatus() != null ? report.getStatus().name() : null,
                entity.getSubmittedAt()
        );
    }
}
