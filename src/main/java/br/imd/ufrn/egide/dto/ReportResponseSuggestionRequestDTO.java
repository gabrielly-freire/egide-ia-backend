package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportResponseSuggestionRequestDTO(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description,
        @JsonProperty("protocol_number") String protocolNumber,
        String category,
        String risk
) {
}
