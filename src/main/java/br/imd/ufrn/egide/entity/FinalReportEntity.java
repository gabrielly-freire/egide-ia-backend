package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

// Relatório final emitido pelo Ouvidor na Fase 3, após análise da defesa do denunciado.
// Nesta fase a decisão é binária (ACATAR / NEGAR) — NEGAR_FALTA_PROVAS é exclusivo da Fase 2.
// O campo defenseId é uma referência ao registro de defesa gerenciado pela Pessoa 2 (módulo de defesa);
// por ora é apenas um BIGINT sem FK enforçada no banco, pois a migration correspondente ainda não foi
// implementada. Quando implementada, será adicionada como FK em migration posterior.
// Existe no máximo 1 registro por manifestação (UNIQUE via JoinColumn).
// Após submissão, o status da manifestação avança para FINAL_ISSUED e o caso entra na fila da OG (Fase 4).
@Data
@Entity
@Table(name = "final_report")
@SQLRestriction(value = "active = true")
public class FinalReportEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "report_id", unique = true, nullable = false)
    private ReportEntity report;

    @ManyToOne
    @JoinColumn(name = "ouvidor_id", nullable = false)
    private UserInfoEntity ouvidor;

    @Column(name = "defense_id")
    private Long defenseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 64)
    private FinalReportDecision decision;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_type", length = 32)
    private PenaltyType penaltyType;

    @Column(name = "penalty_description", columnDefinition = "TEXT")
    private String penaltyDescription;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
}
