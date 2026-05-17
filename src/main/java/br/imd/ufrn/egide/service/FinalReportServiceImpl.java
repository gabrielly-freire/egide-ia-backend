package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;
import br.imd.ufrn.egide.entity.FinalReportEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.FinalReportRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FinalReportServiceImpl implements FinalReportService {

    private final FinalReportRepository finalReportRepository;
    private final ReportService reportService;
    private final UserInfoRepository userInfoRepository;

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

        FinalReportEntity entity = finalReportRepository.findByReportId(reportId)
                .orElseGet(FinalReportEntity::new);
        entity.setReport(report);
        entity.setOuvidor(ouvidor);
        entity.setDefenseId(request.defenseId());
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

        // Avança o caso para aguardar validação do Ouvidor Geral.
        report.setStatus(ReportStatus.FINAL_ISSUED);

        return toDTO(entity, report);
    }

    @Override
    public FinalReportResponseDTO getByReportId(Long reportId) {
        FinalReportEntity entity = finalReportRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Relatório final não encontrado"));
        return toDTO(entity, entity.getReport());
    }

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

    private void ensureAssignedOuvidor(ReportEntity report, UserInfoEntity ouvidor) {
        if (ouvidor.getRole() == Role.ADMIN) {
            return;
        }
        if (report.getOuvidor() == null || !Objects.equals(report.getOuvidor().getId(), ouvidor.getId())) {
            throw new BusinessException(
                    "Você não é o Ouvidor designado para este caso.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

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
