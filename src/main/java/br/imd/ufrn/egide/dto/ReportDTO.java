package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

public record ReportDTO(
        Long id,

        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        String anonymizedDescription,

        String status,

        String category,

        Boolean hasConflict,

        Long userInfoId
) {
}
