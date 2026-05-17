package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO resumido de um caso atribuído ao ouvidor, exibido nos painéis de casos normais e de recurso.
// Reutilizado em dois contextos com semântica diferente:
//   - Painel do ouvidor principal: lista manifestações designadas na Fase 2/3.
//   - Painel do novo ouvidor (Fase 5): lista casos onde foi designado para análise de recurso.
// Anti-viés no contexto de recurso: este DTO deliberadamente NÃO expõe parecer preliminar,
// defesa, relatório final nem histórico de validação da OG, garantindo que o novo ouvidor
// avalie o caso sem influência das conclusões anteriores.
// preliminaryReportIssued indica se já existe um relatório de recurso submetido para o caso
// (usado no contexto de recurso para sinalizar que o ouvidor já concluiu a análise).
public record OuvidorCaseDTO(
        Long id,
        String protocolNumber,
        String title,
        String description,
        LocalDate dateOfOccurrence,
        String status,
        ReportCategory category,
        ReportRisk risk,
        Boolean preliminaryReportIssued,
        LocalDateTime createdAt
) {
}
