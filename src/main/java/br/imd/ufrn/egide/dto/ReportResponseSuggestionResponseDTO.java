package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO de resposta do serviço de IA com a sugestão de resposta institucional para a manifestação.
// suggestedResponse é um texto livre gerado pelo modelo; o ouvidor pode usá-lo ou editá-lo antes de publicar.
public record ReportResponseSuggestionResponseDTO(
        @JsonProperty("report_id") Long reportId,
        @JsonProperty("suggested_response") String suggestedResponse
) {
}
