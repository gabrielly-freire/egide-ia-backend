package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OuvidorCaseDTO(
        Long id,
        String protocolNumber,
        String title,
        String description,
        LocalDate dateOfOccurrence,
        String status,
        ReportCategory category,
        ReportRisk risk,
        Boolean preliminaryReportIssued,
        LocalDateTime createdAt
) {
}
