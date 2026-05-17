package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.GeneralValidationAction;
import br.imd.ufrn.egide.enums.PenaltyType;

import java.time.LocalDateTime;

// DTO de saída com os dados completos do registro de validação da OG (Fase 4).
// Retornado pelas três ações: validate, alter e repass.
// Campos contextuais por ação:
//   - finalReportId: preenchido quando a ação incide sobre o relatório final (Fase 3).
//   - appealReportId: preenchido quando a ação incide sobre o relatório de recurso (Fase 5).
//   - altered*: preenchidos apenas quando action = ALTER.
//   - repassNewOuvidorId / repassNewOuvidorName: preenchidos apenas quando action = REPASS.
//   - repassCountAfter: contador após o repass; útil para o cliente verificar se o caso
//     ainda pode ser repassado novamente (máximo 1 vez — regra de não-loop).
// reportStatus reflete o status atualizado da manifestação após a ação da OG.
public record GeneralValidationResponseDTO(
        Long id,
        Long reportId,
        Long finalReportId,
        Long appealReportId,
        Long ouvidorGeralId,
        String ouvidorGeralName,
        GeneralValidationAction action,
        FinalReportDecision alteredDecision,
        String alteredJustification,
        PenaltyType alteredPenaltyType,
        String alteredPenaltyDescription,
        Long repassNewOuvidorId,
        String repassNewOuvidorName,
        Integer repassCountAfter,
        String reportStatus,
        LocalDateTime decidedAt
) {
}
