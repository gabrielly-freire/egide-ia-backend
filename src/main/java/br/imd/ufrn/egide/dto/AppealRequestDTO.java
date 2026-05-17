package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.AppellantRole;
import jakarta.validation.constraints.NotBlank;

public record AppealRequestDTO(
        AppellantRole appellantRole,

        @NotBlank(message = "Os fundamentos do recurso são obrigatórios")
        String grounds
) {
}
