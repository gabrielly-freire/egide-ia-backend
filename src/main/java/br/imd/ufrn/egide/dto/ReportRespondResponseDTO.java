package br.imd.ufrn.egide.dto;

import java.time.LocalDateTime;

public record ReportRespondResponseDTO(
        Long reportId,
        String responseText,
        String aiSuggestion,
        Boolean usedAiSuggestion,
        String status,
        LocalDateTime respondedAt
) {
}
