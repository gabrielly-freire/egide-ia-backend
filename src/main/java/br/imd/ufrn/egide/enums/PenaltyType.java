package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PenaltyType {
    ADVERTENCIA("Advertência"),
    SUSPENSAO("Suspensão"),
    DEMISSAO("Demissão"),
    OUTRA("Outra");

    private final String description;
}
