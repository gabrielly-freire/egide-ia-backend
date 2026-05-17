package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.validation.constraints.NotNull;

// DTO de entrada para a ação ALTER do Ouvidor Geral na Fase 4.
// Permite que a OG substitua a decisão do ouvidor por sua própria avaliação,
// preservando o relatório original no histórico (GeneralValidationEntity).
// Regras de validação implícitas (enforçadas no service):
//   - alteredDecision é obrigatório; os demais campos dependem da decisão:
//     - Se alteredDecision = ACATAR, alteredPenaltyType deve ser informado.
//     - Se alteredDecision = NEGAR, alteredJustification deve ser informada.
// alteredPenaltyDescription é necessário quando alteredPenaltyType = OUTRA.
public record GeneralValidationAlterRequestDTO(
        @NotNull(message = "A nova decisão é obrigatória")
        FinalReportDecision alteredDecision,
        String alteredJustification,
        PenaltyType alteredPenaltyType,
        String alteredPenaltyDescription
) {
}
