package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportProcessedDTO(
        Long id,

        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "A categoria é obrigatória")
        String category,

        @NotNull(message = "O status é obrigatório")
        String status,

        @NotNull(message = "O indicador de conflito é obrigatório")
        Boolean hasConflict,

        @NotNull(message = "O id da denúncia é obrigatório")
        Long reportId
) {
}
