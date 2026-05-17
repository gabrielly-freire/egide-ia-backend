package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppealStatus {
    OPEN("Aberto"),
    UNDER_ANALYSIS("Em análise pelo novo ouvidor"),
    AWAITING_GENERAL("Aguardando Ouvidor Geral"),
    CLOSED("Encerrado");

    private final String description;
}
