package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;

import java.time.LocalDateTime;

public record FinalReportResponseDTO(
        Long id,
        Long reportId,
        Long ouvidorId,
        String ouvidorName,
        Long defenseId,
        FinalReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        String reportStatus,
        LocalDateTime submittedAt
) {
}
