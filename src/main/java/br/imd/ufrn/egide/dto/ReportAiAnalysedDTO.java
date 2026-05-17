package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO de entrada/saída com o resultado da análise de IA para uma manifestação.
// hasConflict indica se o módulo de IA detectou conflito de interesse entre os responsáveis e a manifestação.
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
