package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportAnonymizedRequestDTO(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description
) { }
