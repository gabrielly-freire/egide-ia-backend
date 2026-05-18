package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.PenaltyType;
import br.imd.ufrn.egide.enums.PreliminaryReportDecision;
import jakarta.validation.constraints.NotNull;

// DTO de entrada para submissão do parecer preliminar pelo Ouvidor na Fase 2.
// Regras de validação implícitas (enforçadas no service, não neste record):
//   - Se decision = ACATAR, penaltyType é obrigatório.
//   - Se decision = NEGAR ou NEGAR_FALTA_PROVAS, justification é obrigatória.
//   - penaltyDescription é obrigatório quando penaltyType = OUTRA.
// O campo aiSuggestion é o texto que o ouvidor recebeu do módulo de IA; quando preenchido,
// o service deriva usedAiSuggestion comparando-o com a justificativa submetida.
// aiSuggestion pode ser nulo se a análise de IA não estiver disponível para o caso.
public record PreliminaryReportRequestDTO(
        @NotNull(message = "A decisão é obrigatória")
        PreliminaryReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        String aiSuggestion,
        Long denouncedUserId
) {
}
