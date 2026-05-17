package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

public record ProofObservationRequestDTO(
        @NotBlank(message = "A observação é obrigatória")
        String observation
) {
}
