package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.PenaltyType;
import br.imd.ufrn.egide.enums.PreliminaryReportDecision;

import java.time.LocalDateTime;

// DTO de saída com os dados completos do parecer preliminar (Fase 2).
// Retornado após submissão e nas consultas de detalhe do caso pelo ouvidor ou pela OG.
// reportStatus reflete o status da manifestação após a operação, permitindo que o
// cliente atualize a exibição sem uma chamada adicional ao endpoint de detalhe do caso.
// usedAiSuggestion pode ser nulo em casos anteriores à integração do módulo de IA.
public record PreliminaryReportResponseDTO(
        Long id,
        Long reportId,
        Long ouvidorId,
        String ouvidorName,
        PreliminaryReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        String aiSuggestion,
        Boolean usedAiSuggestion,
        String reportStatus,
        LocalDateTime submittedAt
) {
}
