package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Tipos de penalidade aplicáveis ao denunciado quando a decisão é ACATAR.
// Representa a graduação de sanções disponíveis no processo disciplinar da Ouvidoria.
// A escolha do tipo orienta a descrição narrativa que deve ser preenchida em penaltyDescription.
// OUTRA existe para cobrir sanções atípicas não previstas nos tipos padronizados,
// sendo obrigatório o preenchimento de penaltyDescription neste caso para garantir clareza jurídica.
@AllArgsConstructor
@Getter
public enum PenaltyType {
    ADVERTENCIA("Advertência"),
    SUSPENSAO("Suspensão"),
    DEMISSAO("Demissão"),
    OUTRA("Outra");

    private final String description;
}
