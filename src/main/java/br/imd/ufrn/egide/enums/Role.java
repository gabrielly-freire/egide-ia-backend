package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    REMONSTRANT("Reclamante"),
    LISTENER("Ouvidor"),
    MANAGER("Gestor"),
    ADMIN("Administrador");

    private String description;
}
