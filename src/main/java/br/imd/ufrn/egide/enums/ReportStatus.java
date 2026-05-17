package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Estados do ciclo de vida da manifestação, do recebimento até o encerramento.
// A transição entre estados é controlada pelos services de cada fase; nenhum estado
// deve ser atribuído diretamente sem passar pelo service responsável.
// Destaques de regra de negócio:
//   - CLOSED_NO_PROOFS: encerramento direto na Fase 2 (decisão NEGAR_FALTA_PROVAS);
//     o denunciado NÃO é notificado, pois não há acusação formal a contestar.
//   - REPASSED: indica que a OG descartou o relatório atual e designou novo ouvidor;
//     o caso retrocede para re-análise sem expor conclusões anteriores ao novo ouvidor.
//   - GENERAL_VALIDATED: estado intermediário após decisão da OG;
//     a partir daqui as partes podem abrir recurso (Fase 5).
//   - CLOSED: encerramento definitivo após validação da OG sobre o AppealReport (Fase 5).
@AllArgsConstructor
@Getter
public enum ReportStatus {
    PENDING("Pendente"),
    ANALYZED("Analisado pela IA"),
    REJECTED("Rejeitado"),
    RESPONDED("Respondido"),
    PRELIMINARY_ISSUED("Parecer preliminar emitido"),
    CLOSED_NO_PROOFS("Encerrado por falta de provas"),
    DEFENSE_OPEN("Em defesa do denunciado"),
    DEFENSE_UNDER_ANALYSIS("Analisando defesa"),
    FINAL_ISSUED("Relatório final emitido"),
    REPASSED("Repassado para novo ouvidor"),
    GENERAL_VALIDATED("Validado pelo Ouvidor Geral"),
    APPEAL_OPEN("Recurso aberto"),
    APPEAL_UNDER_ANALYSIS("Recurso em análise"),
    APPEAL_AWAITING_GENERAL("Recurso aguardando Ouvidor Geral"),
    CLOSED("Encerrado");

    private String description;
}
