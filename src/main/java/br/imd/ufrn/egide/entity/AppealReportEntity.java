package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

// Relatório emitido pelo novo ouvidor ao concluir a análise do(s) recurso(s) — conclusão da Fase 5.
// Possui o mesmo shape do FinalReportEntity (decisão ACATAR/NEGAR, penalidade, justificativa),
// mas refere-se exclusivamente ao julgamento do recurso, não da manifestação original.
// A unicidade por caso é garantida pelo JoinColumn UNIQUE: só existe 1 AppealReport por manifestação,
// independentemente de quantos recursos foram abertos (regra de merge consolida tudo em 1 análise).
// Após a submissão, o caso avança para APPEAL_AWAITING_GENERAL (fila da OG, Fase 4 novamente).
@Data
@Entity
@Table(name = "appeal_report")
@SQLRestriction(value = "active = true")
public class AppealReportEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "report_id", unique = true, nullable = false)
    private ReportEntity report;

    @ManyToOne
    @JoinColumn(name = "new_ouvidor_id", nullable = false)
    private UserInfoEntity newOuvidor;

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
