package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Decisões possíveis no parecer preliminar emitido pelo Ouvidor na Fase 2.
// ACATAR e NEGAR avançam o fluxo para a Fase 3 (defesa do denunciado).
// NEGAR_FALTA_PROVAS é exclusivo desta fase: encerra o caso imediatamente com status
// CLOSED_NO_PROOFS sem abertura de defesa e sem notificação ao denunciado,
// pois a denúncia é considerada insuficientemente embasada para prosseguir.
// Esta opção NÃO existe no FinalReportDecision — o relatório final pressupõe que
// a instrução probatória mínima já foi concluída.
@AllArgsConstructor
@Getter
public enum PreliminaryReportDecision {
    ACATAR("Acatar denúncia"),
    NEGAR("Negar denúncia"),
    NEGAR_FALTA_PROVAS("Encerrar por falta de provas");

    private final String description;
}
