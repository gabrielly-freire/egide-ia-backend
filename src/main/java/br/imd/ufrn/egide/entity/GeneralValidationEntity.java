package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.FinalReportDecision;
import br.imd.ufrn.egide.enums.GeneralValidationAction;
import br.imd.ufrn.egide.enums.PenaltyType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

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
