package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ReportAnalysedRequestDTO(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description,
        List<ReportAiFileProcessing> files,
        @JsonProperty("responsible_users") List<ReportResponsibleUserDTO> responsibleUsers
) { }
