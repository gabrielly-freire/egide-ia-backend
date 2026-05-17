package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.GeneralValidationAlterRequestDTO;
import br.imd.ufrn.egide.dto.GeneralValidationResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorGeralCaseDTO;
import br.imd.ufrn.egide.entity.AppealReportEntity;
import br.imd.ufrn.egide.entity.FinalReportEntity;
import br.imd.ufrn.egide.entity.GeneralValidationEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.GeneralValidationAction;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.enums.Role;
import br.imd.ufrn.egide.repository.AppealReportRepository;
import br.imd.ufrn.egide.repository.FinalReportRepository;
import br.imd.ufrn.egide.repository.GeneralValidationRepository;
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
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneralValidationServiceImpl implements GeneralValidationService {

    private static final List<ReportStatus> PENDING_STATUSES = List.of(
            ReportStatus.FINAL_ISSUED,
            ReportStatus.APPEAL_AWAITING_GENERAL
    );

    private static final int MAX_REPASSES = 1;

    private final ReportService reportService;
    private final FinalReportRepository finalReportRepository;
    private final AppealReportRepository appealReportRepository;
    private final GeneralValidationRepository generalValidationRepository;
    private final UserInfoRepository userInfoRepository;
    private final OuvidorAssignmentService ouvidorAssignmentService;

    @Override
    public List<OuvidorGeralCaseDTO> findPendingCases() {
        requireOuvidorGeral();
        return reportService.findEntitiesByStatusIn(PENDING_STATUSES)
                .stream()
                .map(this::toCaseDTO)
                .sorted(Comparator.comparing(c -> c.pendingSubmittedAt(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    @Transactional
    public GeneralValidationResponseDTO validate(Long reportId) {
        UserInfoEntity geral = requireOuvidorGeral();
        ReportEntity report = reportService.findEntityById(reportId);
        Target target = resolveTarget(report);

        GeneralValidationEntity entity = newEntity(report, target, geral, GeneralValidationAction.VALIDATE);
        entity = generalValidationRepository.save(entity);

        report.setStatus(ReportStatus.GENERAL_VALIDATED);
        closeAppealsIfNeeded(report, target);

        return toDTO(entity, report);
    }

    @Override
    @Transactional
    public GeneralValidationResponseDTO alter(Long reportId, GeneralValidationAlterRequestDTO request) {
        if (request == null || request.alteredDecision() == null) {
            throw new BusinessException("Decisão alterada é obrigatória.", HttpStatus.BAD_REQUEST);
        }

        UserInfoEntity geral = requireOuvidorGeral();
        ReportEntity report = reportService.findEntityById(reportId);
        Target target = resolveTarget(report);

        GeneralValidationEntity entity = newEntity(report, target, geral, GeneralValidationAction.ALTER);
        entity.setAlteredDecision(request.alteredDecision());
        entity.setAlteredJustification(trim(request.alteredJustification()));
        entity.setAlteredPenaltyType(request.alteredPenaltyType());
        entity.setAlteredPenaltyDescription(trim(request.alteredPenaltyDescription()));
        entity = generalValidationRepository.save(entity);

        report.setStatus(ReportStatus.GENERAL_VALIDATED);
        closeAppealsIfNeeded(report, target);

        return toDTO(entity, report);
    }

    @Override
    @Transactional
    public GeneralValidationResponseDTO repass(Long reportId) {
        UserInfoEntity geral = requireOuvidorGeral();
        ReportEntity report = reportService.findEntityById(reportId);
        Target target = resolveTarget(report);

        int currentRepasses = report.getRepassCount() != null ? report.getRepassCount() : 0;
        if (currentRepasses >= MAX_REPASSES) {
            throw new BusinessException(
                    "Este caso já foi repassado uma vez. A validação ou alteração é obrigatória.",
                    HttpStatus.CONFLICT
            );
        }

        List<Long> excluded = collectOuvidorIdsAlreadyOnCase(report);
        UserInfoEntity newOuvidor = ouvidorAssignmentService.assignOuvidor(excluded);

        report.setRepassCount(currentRepasses + 1);
        report.setOuvidor(newOuvidor);

        report.setStatus(ReportStatus.REPASSED);

        if (target.finalReport != null) {
            finalReportRepository.delete(target.finalReport);
        }

        GeneralValidationEntity entity = newEntity(report, target, geral, GeneralValidationAction.REPASS);
        entity.setRepassNewOuvidor(newOuvidor);
        entity = generalValidationRepository.save(entity);

        return toDTO(entity, report);
    }

    private void closeAppealsIfNeeded(ReportEntity report, Target target) {
        if (target.appealReport == null) {
            return;
        }
        report.setStatus(ReportStatus.CLOSED);
    }

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

    private UserInfoEntity requireOuvidorGeral() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfoEntity user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
        if (user.getRole() != Role.GENERAL_LISTENER && user.getRole() != Role.ADMIN) {
            throw new BusinessException(
                    "Apenas o Ouvidor Geral pode executar esta operação.",
                    HttpStatus.FORBIDDEN
            );
        }
        return user;
    }

    private Target resolveTarget(ReportEntity report) {
        ReportStatus s = report.getStatus();
        if (s == ReportStatus.FINAL_ISSUED) {
            FinalReportEntity fr = finalReportRepository.findByReportId(report.getId())
                    .orElseThrow(() -> new BusinessException(
                            "Relatório final ainda não foi submetido.",
                            HttpStatus.CONFLICT
                    ));
            return new Target(fr, null);
        }
        if (s == ReportStatus.APPEAL_AWAITING_GENERAL) {
            AppealReportEntity ar = appealReportRepository.findByReportId(report.getId())
                    .orElseThrow(() -> new BusinessException(
                            "Relatório do recurso ainda não foi submetido.",
                            HttpStatus.CONFLICT
                    ));
            return new Target(null, ar);
        }
        throw new BusinessException(
                "Caso não está pendente de validação pelo Ouvidor Geral (status atual: " + s + ").",
                HttpStatus.CONFLICT
        );
    }

    private GeneralValidationEntity newEntity(ReportEntity report,
                                              Target target,
                                              UserInfoEntity geral,
                                              GeneralValidationAction action) {
        GeneralValidationEntity entity = new GeneralValidationEntity();
        entity.setReport(report);
        entity.setFinalReport(target.finalReport);
        entity.setAppealReport(target.appealReport);
        entity.setOuvidorGeral(geral);
        entity.setAction(action);
        entity.setDecidedAt(LocalDateTime.now());
        return entity;
    }

    private OuvidorGeralCaseDTO toCaseDTO(ReportEntity report) {
        boolean isAppeal = report.getStatus() == ReportStatus.APPEAL_AWAITING_GENERAL;
        int repassCount = report.getRepassCount() != null ? report.getRepassCount() : 0;
        boolean canRepass = !isAppeal && repassCount < MAX_REPASSES;

        FinalReportEntity fr = report.getFinalReport();
        AppealReportEntity ar = report.getAppealReport();

        return new OuvidorGeralCaseDTO(
                report.getId(),
                report.getProtocolNumber(),
                report.getTitle(),
                report.getStatus() != null ? report.getStatus().name() : null,
                repassCount,
                canRepass,
                isAppeal,
                isAppeal && ar != null ? ar.getDecision() : (fr != null ? fr.getDecision() : null),
                isAppeal && ar != null ? ar.getSubmittedAt() : (fr != null ? fr.getSubmittedAt() : null)
        );
    }

    private GeneralValidationResponseDTO toDTO(GeneralValidationEntity entity, ReportEntity report) {
        return new GeneralValidationResponseDTO(
                entity.getId(),
                report.getId(),
                entity.getFinalReport() != null ? entity.getFinalReport().getId() : null,
                entity.getAppealReport() != null ? entity.getAppealReport().getId() : null,
                entity.getOuvidorGeral() != null ? entity.getOuvidorGeral().getId() : null,
                entity.getOuvidorGeral() != null ? entity.getOuvidorGeral().getName() : null,
                entity.getAction(),
                entity.getAlteredDecision(),
                entity.getAlteredJustification(),
                entity.getAlteredPenaltyType(),
                entity.getAlteredPenaltyDescription(),
                entity.getRepassNewOuvidor() != null ? entity.getRepassNewOuvidor().getId() : null,
                entity.getRepassNewOuvidor() != null ? entity.getRepassNewOuvidor().getName() : null,
                report.getRepassCount(),
                report.getStatus() != null ? report.getStatus().name() : null,
                entity.getDecidedAt()
        );
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private record Target(FinalReportEntity finalReport, AppealReportEntity appealReport) { }
}
