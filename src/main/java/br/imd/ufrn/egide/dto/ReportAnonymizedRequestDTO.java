package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO de requisição para o endpoint de anonimização do serviço de IA.
// Encaminha o título e a descrição originais da manifestação para remoção de dados pessoais.
public record ReportAnonymizedRequestDTO(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description
) { }
