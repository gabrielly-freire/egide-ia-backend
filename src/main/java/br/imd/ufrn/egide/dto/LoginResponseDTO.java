package br.imd.ufrn.egide.dto;

public record LoginResponseDTO(
        String token,
        String tokenType,
        Long expiresIn
) {
}
