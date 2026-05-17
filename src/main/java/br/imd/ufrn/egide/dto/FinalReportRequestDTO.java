package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.validation.constraints.NotNull;

// DTO de entrada para submissão do relatório final pelo Ouvidor na Fase 3.
// Reutilizado também pelo novo ouvidor ao submeter o AppealReport na Fase 5
// (endpoint POST /recurso/report/{id}/relatorio), pois o shape é idêntico.
// Regras de validação implícitas (enforçadas no service):
//   - Se decision = ACATAR, penaltyType é obrigatório.
//   - Se decision = NEGAR, justification é obrigatória.
// O campo defenseId referencia o registro de defesa gerenciado pelo módulo da Pessoa 2;
// é opcional até que a FK seja formalmente implementada em migration posterior.
public record FinalReportRequestDTO(
        @NotNull(message = "A decisão é obrigatória")
        FinalReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        Long defenseId
) {
}
