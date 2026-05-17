package br.imd.ufrn.egide.enums;

// Nível de risco atribuído à manifestação pelo módulo de IA, em ordem decrescente de gravidade.
// Influencia a priorização dos casos na fila do ouvidor e pode acionar alertas de SLA.
public enum ReportRisk {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}
