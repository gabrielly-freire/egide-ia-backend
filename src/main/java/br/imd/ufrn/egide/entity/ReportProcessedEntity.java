package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@Table(name = "report_processed")
@SQLRestriction(value = "active = true")
public class ReportProcessedEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private Boolean hasConflict;

    @OneToOne
    @JoinColumn(name = "report_id", unique = true)
    private ReportEntity report;
}
