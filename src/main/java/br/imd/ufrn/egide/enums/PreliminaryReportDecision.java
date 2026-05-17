package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PreliminaryReportDecision {
    ACATAR("Acatar denúncia"),
    NEGAR("Negar denúncia"),
    NEGAR_FALTA_PROVAS("Encerrar por falta de provas");

    private final String description;
}
