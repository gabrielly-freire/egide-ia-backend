package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinalReportDecision {
    ACATAR("Acatar denúncia"),
    NEGAR("Negar denúncia");

    private final String description;
}
