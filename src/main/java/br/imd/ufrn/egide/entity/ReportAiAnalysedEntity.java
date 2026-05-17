package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.ReportCategory;
import br.imd.ufrn.egide.enums.ReportRisk;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "report_ai_analysed")
@SQLRestriction(value = "active = true")
// Entidade que armazena o resultado da análise automática de IA para uma manifestação.
// Gerada assincronamente pelo pipeline de IA após a criação da manifestação.
// conflictedUserIds é uma coleção de elementos (tabela auxiliar) contendo os IDs dos usuários
// com conflito de interesse detectado pelo modelo de IA.
// managerConflict sinaliza especificamente conflito envolvendo um MANAGER, ativando ocultação de dados.
public class ReportAiAnalysedEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String titleAnonymized;

    @Column(columnDefinition = "TEXT")
    private String descriptionAnonymized;

    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @Enumerated(EnumType.STRING)
    private ReportRisk risk;

    @Column(name = "conflict_detected")
    private Boolean conflictDetected;

    @Column(name = "manager_conflict")
    private Boolean managerConflict;

    @ElementCollection
    @CollectionTable(
            name = "report_ai_analysed_conflicted_user_ids",
            joinColumns = @JoinColumn(name = "report_ai_analysed_id")
    )
    @Column(name = "conflicted_user_id")
    private List<String> conflictedUserIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;
}
