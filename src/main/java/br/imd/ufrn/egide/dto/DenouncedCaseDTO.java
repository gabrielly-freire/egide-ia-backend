package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.ReportStatus;
import java.time.LocalDateTime;
import java.util.List;

public record DenouncedCaseDTO(
        Long reportId,
        String protocolNumber,
        String title,
        String description,
        ReportStatus status,
        LocalDateTime createdAt,
        DenouncedPreliminaryReportDTO preliminaryReport,
        List<FileDTO> evidenceFiles
) {
}
