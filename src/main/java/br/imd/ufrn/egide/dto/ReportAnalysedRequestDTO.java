package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// DTO de requisição para o endpoint de classificação do serviço de IA.
// Inclui os arquivos de evidência codificados em base64 e a lista de usuários responsáveis
// para detecção de conflito de interesse.
public record ReportAnalysedRequestDTO(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description,
        List<ReportAiFileProcessing> files,
        @JsonProperty("responsible_users") List<ReportResponsibleUserDTO> responsibleUsers
) { }
