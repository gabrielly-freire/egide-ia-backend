package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;

import java.time.LocalDateTime;

// DTO de saída com os dados completos do relatório final (Fase 3).
// Reutilizado também como resposta do AppealReport (Fase 5), onde defenseId será nulo
// pois o relatório de recurso não possui vínculo com o registro de defesa.
// reportStatus reflete o status atualizado da manifestação após a operação,
// evitando chamada adicional ao endpoint de detalhe.
// ouvidorId e ouvidorName identificam o responsável pelo relatório, que pode ser
// o ouvidor original (Fase 3) ou o novo ouvidor designado (Fase 5).
public record FinalReportResponseDTO(
        Long id,
        Long reportId,
        Long ouvidorId,
        String ouvidorName,
        Long defenseId,
        FinalReportDecision decision,
        String justification,
        PenaltyType penaltyType,
        String penaltyDescription,
        String reportStatus,
        LocalDateTime submittedAt
) {
}
