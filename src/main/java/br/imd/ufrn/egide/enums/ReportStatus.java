package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
