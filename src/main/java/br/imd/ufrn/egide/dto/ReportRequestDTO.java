package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ReportRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        LocalDate dateOfOccurrence
) {
}
