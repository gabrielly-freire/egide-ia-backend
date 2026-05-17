package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Estados do ciclo de vida de um recurso individual na Fase 5.
// Cada AppealEntity possui seu próprio status, mas a progressão é coordenada:
//   - OPEN: recurso registrado, aguardando designação do novo ouvidor (não usado atualmente,
//     pois o ouvidor é designado no momento da submissão pelo AppealServiceImpl).
//   - UNDER_ANALYSIS: novo ouvidor designado e analisando; status atribuído na submissão.
//   - AWAITING_GENERAL: novo ouvidor submeteu o AppealReport; caso entra na fila da OG.
//   - CLOSED: OG concluiu a validação/alteração do AppealReport; recurso encerrado definitivamente.
// Quando ambas as partes recorrem (regra de merge), ambos os registros de AppealEntity
// transitam sincronicamente entre AWAITING_GENERAL e CLOSED pelo AppealServiceImpl.
@AllArgsConstructor
@Getter
public enum AppealStatus {
    OPEN("Aberto"),
    UNDER_ANALYSIS("Em análise pelo novo ouvidor"),
    AWAITING_GENERAL("Aguardando Ouvidor Geral"),
    CLOSED("Encerrado");

    private final String description;
}
