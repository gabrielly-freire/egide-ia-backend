package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O username é obrigatório")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
