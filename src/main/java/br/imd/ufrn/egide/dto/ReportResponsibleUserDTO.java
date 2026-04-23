package br.imd.ufrn.egide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportResponsibleUserDTO(
        String id,
        String name,
        String email,
        @JsonProperty("user_name") String userName,
        String role
) { }
