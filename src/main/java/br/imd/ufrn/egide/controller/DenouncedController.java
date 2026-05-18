package br.imd.ufrn.egide.controller;

import br.imd.ufrn.egide.dto.DenouncedCaseDTO;
import br.imd.ufrn.egide.dto.DenouncedPreliminaryReportDTO;
import br.imd.ufrn.egide.dto.FileDTO;
import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.PreliminaryReportEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.entity.UserInfoEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.repository.UserInfoRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/denunciado")
@Tag(name = "Denunciado", description = "Acesso do denunciado aos casos")
@RequiredArgsConstructor
public class DenouncedController {

    private final ReportRepository reportRepository;
    private final UserInfoRepository userInfoRepository;

    @GetMapping("/casos")
    @PreAuthorize("hasAnyRole('REMONSTRANT','LISTENER','GENERAL_LISTENER','MANAGER','ADMIN')")
    @Operation(summary = "Listar casos contra mim (Fase 3 ou posterior)")
    @Transactional(readOnly = true)
    public ResponseEntity<List<DenouncedCaseDTO>> listMyCases() {
        UserInfoEntity currentUser = getCurrentUser();

        List<ReportStatus> statuses = List.of(
                ReportStatus.DEFENSE_OPEN,
                ReportStatus.DEFENSE_UNDER_ANALYSIS,
                ReportStatus.FINAL_ISSUED,
                ReportStatus.GENERAL_VALIDATED,
                ReportStatus.APPEAL_OPEN,
                ReportStatus.APPEAL_UNDER_ANALYSIS,
                ReportStatus.APPEAL_AWAITING_GENERAL,
                ReportStatus.CLOSED
        );

        List<DenouncedCaseDTO> cases = reportRepository
                .findByDenouncedUserIdAndStatusIn(currentUser.getId(), statuses)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(cases);
    }

    private DenouncedCaseDTO toDTO(ReportEntity report) {
        List<FileDTO> evidence = report.getFiles() == null
                ? List.of()
                : report.getFiles().stream().map(this::toFileDTO).toList();

        return new DenouncedCaseDTO(
                report.getId(),
                report.getProtocolNumber(),
                report.getTitle(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt(),
                toPreliminaryDTO(report.getPreliminaryReport()),
                evidence
        );
    }

    private DenouncedPreliminaryReportDTO toPreliminaryDTO(PreliminaryReportEntity preliminaryReport) {
        if (preliminaryReport == null) {
            return null;
        }
        return new DenouncedPreliminaryReportDTO(
                preliminaryReport.getDecision(),
                preliminaryReport.getJustification(),
                preliminaryReport.getPenaltyType(),
                preliminaryReport.getPenaltyDescription(),
                preliminaryReport.getSubmittedAt()
        );
    }

    private FileDTO toFileDTO(FileEntity file) {
        return new FileDTO(file.getId(), file.getName(), file.getContentType(), file.getSize());
    }

    private UserInfoEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }
        return userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}
