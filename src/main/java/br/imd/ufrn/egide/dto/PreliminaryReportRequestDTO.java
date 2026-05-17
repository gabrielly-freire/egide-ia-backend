package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.PenaltyType;
import br.imd.ufrn.egide.enums.PreliminaryReportDecision;
import jakarta.validation.constraints.NotNull;

public record PreliminaryReportRequestDTO(
        @NotNull(message = "A decisão é obrigatória")
        PreliminaryReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        String aiSuggestion
) {
}
