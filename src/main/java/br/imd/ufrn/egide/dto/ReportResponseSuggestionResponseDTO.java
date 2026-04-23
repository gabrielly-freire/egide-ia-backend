package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportResponseSuggestionResponseDTO(
        @JsonProperty("report_id") Long reportId,
        @JsonProperty("suggested_response") String suggestedResponse
) {
}
