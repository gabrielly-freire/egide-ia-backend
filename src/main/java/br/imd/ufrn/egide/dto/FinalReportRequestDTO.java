package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.validation.constraints.NotNull;

public record FinalReportRequestDTO(
        @NotNull(message = "A decisão é obrigatória")
        FinalReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        Long defenseId
) {
}
