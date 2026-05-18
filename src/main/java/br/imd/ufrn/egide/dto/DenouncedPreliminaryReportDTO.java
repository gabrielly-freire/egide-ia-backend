package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.PenaltyType;
import br.imd.ufrn.egide.enums.PreliminaryReportDecision;
import java.time.LocalDateTime;

public record DenouncedPreliminaryReportDTO(
        PreliminaryReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        LocalDateTime submittedAt
) {
}
