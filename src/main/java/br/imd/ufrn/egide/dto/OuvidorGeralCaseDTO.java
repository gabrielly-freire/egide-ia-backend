package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.FinalReportDecision;

import java.time.LocalDateTime;

// DTO resumido de um caso pendente de decisão pelo Ouvidor Geral, exibido no painel da Fase 4.
// Agrega apenas os campos necessários para a listagem e tomada de decisão pela OG,
// sem expor o conteúdo completo dos relatórios para não sobrecarregar a resposta.
// Destaques de regra de negócio:
//   - canRepass: falso se o caso já atingiu o limite de 1 repass ou se está em fase de recurso
//     (APPEAL_AWAITING_GENERAL); a OG não pode repassar um AppealReport.
//   - isAppealReport: diferencia se a OG está avaliando o FinalReport (Fase 3) ou o AppealReport (Fase 5).
//   - pendingDecision: decisão do relatório aguardando validação (FinalReport ou AppealReport, conforme o caso).
//   - pendingSubmittedAt: usado para ordenação cronológica da fila no painel da OG.
public record OuvidorGeralCaseDTO(
        Long id,
        String protocolNumber,
        String title,
        String status,
        Integer repassCount,
        Boolean canRepass,
        Boolean isAppealReport,
        FinalReportDecision pendingDecision,
        LocalDateTime pendingSubmittedAt
) {
}
