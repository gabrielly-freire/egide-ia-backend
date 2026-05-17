package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.PenaltyType;
import br.imd.ufrn.egide.enums.PreliminaryReportDecision;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

// Parecer preliminar emitido pelo Ouvidor na Fase 2 do fluxo de Ouvidoria.
// Registra a decisão (ACATAR / NEGAR / NEGAR_FALTA_PROVAS), a penalidade sugerida
// e a rastreabilidade de uso da IA: aiSuggestion armazena o texto sugerido pelo modelo,
// enquanto usedAiSuggestion indica se o ouvidor adotou a sugestão — dado estratégico
// para auditoria de dependência humana-IA no processo decisório.
// Decisão NEGAR_FALTA_PROVAS é exclusiva desta fase; encerra o caso sem notificar o denunciado
// e transiciona o status para CLOSED_NO_PROOFS.
// Existe no máximo 1 registro por manifestação (UNIQUE via JoinColumn).
@Data
@Entity
@Table(name = "preliminary_report")
@SQLRestriction(value = "active = true")
public class PreliminaryReportEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "report_id", unique = true, nullable = false)
    private ReportEntity report;

    @ManyToOne
    @JoinColumn(name = "ouvidor_id", nullable = false)
    private UserInfoEntity ouvidor;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 64)
    private PreliminaryReportDecision decision;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_type", length = 32)
    private PenaltyType penaltyType;

    @Column(name = "penalty_description", columnDefinition = "TEXT")
    private String penaltyDescription;

    @Column(name = "ai_suggestion", columnDefinition = "TEXT")
    private String aiSuggestion;

    @Column(name = "used_ai_suggestion")
    private Boolean usedAiSuggestion;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
}
