package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "report_ai_analysed")
@SQLRestriction(value = "active = true")
public class ReportAiAnalysedEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titleAnonymized;

    private String descriptionAnonymized;

    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @Enumerated(EnumType.STRING)
    private ReportRisk risk;

    private Boolean hasConflict;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;
}
