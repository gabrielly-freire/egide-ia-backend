package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

// DTO de criação de manifestação pelo denunciante (REMONSTRANT).
// dateOfOccurrence é opcional; quando nulo, a data de registro é usada como referência.
public record ReportRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        LocalDate dateOfOccurrence
) {
}
