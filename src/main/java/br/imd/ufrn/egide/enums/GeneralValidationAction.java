package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GeneralValidationAction {
    VALIDATE("Validar"),
    ALTER("Alterar parecer"),
    REPASS("Repassar para novo ouvidor");

    private final String description;
}
