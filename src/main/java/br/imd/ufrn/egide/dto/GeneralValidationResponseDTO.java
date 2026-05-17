package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.GeneralValidationAction;
import br.imd.ufrn.egide.enums.PenaltyType;

import java.time.LocalDateTime;

public record GeneralValidationResponseDTO(
        Long id,
        Long reportId,
        Long finalReportId,
        Long appealReportId,
        Long ouvidorGeralId,
        String ouvidorGeralName,
        GeneralValidationAction action,
        FinalReportDecision alteredDecision,
        String alteredJustification,
        PenaltyType alteredPenaltyType,
        String alteredPenaltyDescription,
        Long repassNewOuvidorId,
        String repassNewOuvidorName,
        Integer repassCountAfter,
        String reportStatus,
        LocalDateTime decidedAt
) {
}
