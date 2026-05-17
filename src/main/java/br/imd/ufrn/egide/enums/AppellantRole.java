package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppellantRole {
    DENUNCIANTE("Denunciante"),
    DENUNCIADO("Denunciado");

    private final String description;
}
