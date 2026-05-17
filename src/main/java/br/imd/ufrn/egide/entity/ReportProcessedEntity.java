package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;
import br.imd.ufrn.egide.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@Table(name = "report_processed")
@SQLRestriction(value = "active = true")
// Entidade de triagem que consolida o resultado do processamento pós-IA de uma manifestação.
// Criada ou atualizada pelo ReportProcessedService após confirmação manual ou automática dos dados de IA.
// Possui relação @OneToOne exclusiva com ReportEntity (unique = true no join column).
public class ReportProcessedEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String titleAnonymized;

    @Column(columnDefinition = "TEXT")
    private String descriptionAnonymized;

    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @Enumerated(EnumType.STRING)
    private ReportRisk risk;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private Boolean hasConflict;

    @OneToOne
    @JoinColumn(name = "report_id", unique = true)
    private ReportEntity report;
}
