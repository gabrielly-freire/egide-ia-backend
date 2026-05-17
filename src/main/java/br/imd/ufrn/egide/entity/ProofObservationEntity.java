package br.imd.ufrn.egide.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@Table(name = "proof_observation")
@SQLRestriction(value = "active = true")
public class ProofObservationEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private FileEntity file;

    @ManyToOne
    @JoinColumn(name = "preliminary_report_id")
    private PreliminaryReportEntity preliminaryReport;

    @ManyToOne
    @JoinColumn(name = "ouvidor_id", nullable = false)
    private UserInfoEntity ouvidor;

    @Column(name = "observation", nullable = false, columnDefinition = "TEXT")
    private String observation;
}
