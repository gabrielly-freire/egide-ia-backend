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
// Entidade de pesquisa de satisfação vinculada a uma manifestação encerrada.
// Regra de negócio: apenas uma pesquisa por manifestação é permitida; a unicidade é garantida
// pela verificação no ReportServiceImpl antes da persistência.
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