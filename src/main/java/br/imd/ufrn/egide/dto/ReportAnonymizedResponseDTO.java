package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO de resposta do serviço de IA com o título e a descrição anonimizados da manifestação.
// Os campos mapeiam o contrato JSON do microsserviço (snake_case -> camelCase via @JsonProperty).
public record ReportAnonymizedResponseDTO(
        @JsonProperty("report_id") Long reportId,
        @JsonProperty("anonymized_title") String anonymizedTitle,
        @JsonProperty("anonymized_description") String anonymizedDescription

) { }
