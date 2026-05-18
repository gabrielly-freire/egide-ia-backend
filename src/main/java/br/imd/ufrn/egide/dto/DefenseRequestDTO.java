package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

public record DefenseRequestDTO(
        @NotBlank(message = "O texto da defesa é obrigatório")
        String defenseText
) {
}
