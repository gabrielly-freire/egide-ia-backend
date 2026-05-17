package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.PenaltyType;
import br.imd.ufrn.egide.enums.PreliminaryReportDecision;

import java.time.LocalDateTime;

public record PreliminaryReportResponseDTO(
        Long id,
        Long reportId,
        Long ouvidorId,
        String ouvidorName,
        PreliminaryReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        String aiSuggestion,
        Boolean usedAiSuggestion,
        String reportStatus,
        LocalDateTime submittedAt
) {
}
