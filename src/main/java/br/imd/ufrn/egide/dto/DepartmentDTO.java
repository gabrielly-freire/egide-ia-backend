package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

// DTO de entrada e saída para operações de departamento.
// Usado tanto na criação (id nulo) quanto na leitura (id preenchido pelo servidor).
public record DepartmentDTO(
        Long id,

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "A sigla é obrigatória")
        String acronym
) {
}
