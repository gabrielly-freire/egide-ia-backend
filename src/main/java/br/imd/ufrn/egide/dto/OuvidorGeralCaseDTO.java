package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;

import java.time.LocalDateTime;

public record OuvidorGeralCaseDTO(
        Long id,
        String protocolNumber,
        String title,
        String status,
        Integer repassCount,
        Boolean canRepass,
        Boolean isAppealReport,
        FinalReportDecision pendingDecision,
        LocalDateTime pendingSubmittedAt
) {
}
