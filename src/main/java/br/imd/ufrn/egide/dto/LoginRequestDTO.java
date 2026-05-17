package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

// DTO de requisição de autenticação; recebe credenciais do usuário para geração do token JWT.
public record LoginRequestDTO(
        @NotBlank(message = "O username é obrigatório")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
