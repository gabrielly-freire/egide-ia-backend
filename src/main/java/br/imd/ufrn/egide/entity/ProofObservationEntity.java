package br.imd.ufrn.egide.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

// Anotação textual do ouvidor sobre um arquivo de prova vinculado à manifestação.
// Permite que o ouvidor registre observações analíticas sobre evidências específicas
// durante a Fase 2 (análise do parecer preliminar), associando cada comentário ao
// arquivo examinado e ao parecer preliminar em elaboração.
// Múltiplas observações podem existir por arquivo, refletindo análises detalhadas
// de um mesmo documento ou mídia ao longo do tempo.
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
