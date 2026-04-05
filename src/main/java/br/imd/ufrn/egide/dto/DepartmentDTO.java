package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentDTO(
        Long id,

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "A sigla é obrigatória")
        String acronym
) {
}
