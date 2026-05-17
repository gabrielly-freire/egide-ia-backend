package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.validation.constraints.NotNull;


public record GeneralValidationAlterRequestDTO(
        @NotNull(message = "A nova decisão é obrigatória")
        FinalReportDecision alteredDecision,
        String alteredJustification,
        PenaltyType alteredPenaltyType,
        String alteredPenaltyDescription
) {
}
