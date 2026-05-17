package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Tipos de ação executáveis pelo Ouvidor Geral na Fase 4 do fluxo de Ouvidoria.
// Cada ação gera um registro auditável em GeneralValidationEntity:
//   - VALIDATE: OG concorda com o relatório; nenhum campo de alteração é preenchido.
//   - ALTER: OG discorda da decisão ou penalidade e registra a sua própria;
//     os campos altered* em GeneralValidationEntity devem ser preenchidos.
//   - REPASS: OG entende que o caso precisa de nova análise imparcial;
//     um novo ouvidor é sorteado (anti-viés) e o relatório atual é descartado.
//     Limitado a 1 vez por caso pela regra de não-loop (repassCount no ReportEntity).
@AllArgsConstructor
@Getter
public enum GeneralValidationAction {
    VALIDATE("Validar"),
    ALTER("Alterar parecer"),
    REPASS("Repassar para novo ouvidor");

    private final String description;
}
