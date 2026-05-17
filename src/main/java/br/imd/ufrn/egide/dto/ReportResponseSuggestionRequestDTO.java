package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO de requisição para o endpoint de sugestão de resposta do serviço de IA.
// Inclui categoria e risco para que o modelo gere uma resposta contextualizada ao tipo da manifestação.
public record ReportResponseSuggestionRequestDTO(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description,
        @JsonProperty("protocol_number") String protocolNumber,
        String category,
        String risk
) {
}
