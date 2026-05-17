package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.GeneralValidationAction;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

// Registro auditável de cada ação executada pelo Ouvidor Geral na Fase 4.
// Cada decisão (VALIDATE, ALTER, REPASS) gera um novo registro independente,
// permitindo rastrear o histórico completo de ações da OG sobre um caso.
// Os campos finalReport e appealReport são mutuamente exclusivos por ação:
//   - finalReport é preenchido quando a OG avalia um relatório da Fase 3 (status FINAL_ISSUED);
//   - appealReport é preenchido quando a OG avalia um relatório da Fase 5 (status APPEAL_AWAITING_GENERAL).
// Os campos altered* só fazem sentido quando action = ALTER; permanecem nulos nas demais ações.
// O campo repassNewOuvidor só é preenchido quando action = REPASS.
@Data
@Entity
@Table(name = "general_validation")
@SQLRestriction(value = "active = true")
public class GeneralValidationEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    @ManyToOne
    @JoinColumn(name = "final_report_id")
    private FinalReportEntity finalReport;

    @ManyToOne
    @JoinColumn(name = "appeal_report_id")
    private AppealReportEntity appealReport;

    @ManyToOne
    @JoinColumn(name = "ouvidor_geral_id", nullable = false)
    private UserInfoEntity ouvidorGeral;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 32)
    private GeneralValidationAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "altered_decision", length = 64)
    private FinalReportDecision alteredDecision;

    @Column(name = "altered_justification", columnDefinition = "TEXT")
    private String alteredJustification;

    @Enumerated(EnumType.STRING)
    @Column(name = "altered_penalty_type", length = 32)
    private PenaltyType alteredPenaltyType;

    @Column(name = "altered_penalty_description", columnDefinition = "TEXT")
    private String alteredPenaltyDescription;

    @ManyToOne
    @JoinColumn(name = "repass_new_ouvidor_id")
    private UserInfoEntity repassNewOuvidor;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;
}
