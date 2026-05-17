package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Decisões possíveis no relatório final (Fase 3) e no relatório de recurso (Fase 5).
// A decisão é intencionalmente binária: ACATAR ou NEGAR.
// NEGAR_FALTA_PROVAS não existe neste enum — é exclusivo da Fase 2 (PreliminaryReportDecision),
// pois até chegar ao relatório final o caso já passou pela defesa do denunciado,
// tornando o encerramento por falta de provas inapropriado neste estágio.
// Este enum também é reutilizado pelo Ouvidor Geral em GeneralValidationEntity.alteredDecision
// quando a OG decide alterar o parecer (action = ALTER).
@AllArgsConstructor
@Getter
public enum FinalReportDecision {
    ACATAR("Acatar denúncia"),
    NEGAR("Negar denúncia");

    private final String description;
}
