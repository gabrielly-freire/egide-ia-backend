package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO que representa um usuário responsável enviado ao serviço de IA para detecção de conflito de interesse.
// O campo id é string para compatibilidade com o contrato do microsserviço de IA.
public record ReportResponsibleUserDTO(
        String id,
        String name,
        String email,
        @JsonProperty("user_name") String userName,
        String role
) { }
