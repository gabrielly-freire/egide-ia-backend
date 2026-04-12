package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportAnonymizedResponseDTO(
        @JsonProperty("report_id") Long reportId,
        @JsonProperty("anonymized_title") String anonymizedTitle,
        @JsonProperty("anonymized_description") String anonymizedDescription

) { }
