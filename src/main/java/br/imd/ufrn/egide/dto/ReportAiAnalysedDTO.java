package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportAiAnalysedDTO(
        Long id,

        @NotBlank(message = "O título anonimizado é obrigatório")
        String titleAnonymized,

        @NotBlank(message = "A descrição anonimizada é obrigatória")
        String descriptionAnonymized,

        String category,

        String risk,

        Boolean hasConflict,

        @NotNull(message = "O id da denúncia é obrigatório")
        Long reportId
) {
}
