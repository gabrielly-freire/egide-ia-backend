package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportAnalysedResponseDTO(
        @JsonProperty("report_id") Long reportId,
        ReportCategory category,
        @JsonProperty("risk_level") ReportRisk risk
) { }
