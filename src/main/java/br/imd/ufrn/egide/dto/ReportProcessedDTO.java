package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO de entrada/saída para a entidade de triagem pós-IA (ReportProcessedEntity).
// Consolida categoria, risco, status e indicador de conflito atribuídos após a análise automatizada.
public record ReportProcessedDTO(
        Long id,

        @NotBlank(message = "O título anonimizado é obrigatório")
        String titleAnonymized,

        @NotBlank(message = "A descrição anonimizada é obrigatória")
        String descriptionAnonymized,

        @NotNull(message = "A categoria é obrigatória")
        String category,

        @NotNull(message = "O risco é obrigatório")
        String risk,

        @NotNull(message = "O status é obrigatório")
        String status,

        @NotNull(message = "O indicador de conflito é obrigatório")
        Boolean hasConflict,

        @NotNull(message = "O id da denúncia é obrigatório")
        Long reportId
) {
}
