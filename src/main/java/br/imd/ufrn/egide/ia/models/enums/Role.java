package br.imd.ufrn.egide.ia.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("Administrador"),
    OMBUDSMAN("Ouvidor"),
    USER("Usuário comum");

    private String description;
}
