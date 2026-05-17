package br.imd.ufrn.egide.dto;

// DTO de resposta do login; contém o token JWT, seu tipo (sempre "Bearer") e a validade em segundos.
public record LoginResponseDTO(
        String token,
        String tokenType,
        Long expiresIn
) {
}
