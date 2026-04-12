package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportAiFileProcessing(
        String filename,
        @JsonProperty("mime_type") String mimeType,
        @JsonProperty("base64_data") String base64Data
) { }
