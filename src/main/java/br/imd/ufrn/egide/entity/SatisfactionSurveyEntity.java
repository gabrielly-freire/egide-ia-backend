package br.imd.ufrn.egide.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "satisfaction_survey")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SatisfactionSurveyEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    @Column(nullable = false)
    private Integer speedRating;

    @Column(nullable = false)
    private Integer resolutionRating;

    @Column(columnDefinition = "TEXT")
    private String comments;

}