package br.imd.ufrn.egide.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportStatus {
    PENDING("Pendente"),
    ANALYZED("Analisado"),
    REJECTED("Rejeitado");

    private String description;
}