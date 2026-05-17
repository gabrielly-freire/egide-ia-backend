package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Entidade central da manifestação no sistema de Ouvidoria.
// Agrega todos os vínculos do ciclo de vida: ouvidor designado (sorteado da pool), denunciado,
// parecer preliminar (Fase 2), relatório final (Fase 3), recursos (Fase 5) e relatório de recurso.
// O campo repassCount controla a regra de não-loop da OG: a OG só pode repassar um caso 1 vez;
// tentativas subsequentes são bloqueadas no GeneralValidationServiceImpl.
// O filtro @SQLRestriction garante que registros com active = false (soft-delete) sejam invisíveis
// automaticamente para todas as queries JPA, sem necessidade de cláusula manual.
@Data
@Entity
@Table(name = "report")
@SQLRestriction(value = "active = true")
public class ReportEntity extends BaseEntity {

    @Column(unique = true)
    private String protocolNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private LocalDate dateOfOccurrence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @ManyToOne
    @JoinColumn(name = "user_info_id")
    private UserInfoEntity userInfo;

    @ManyToOne
    @JoinColumn(name = "ouvidor_id")
    private UserInfoEntity ouvidor;

    @ManyToOne
    @JoinColumn(name = "denunciado_user_id")
    private UserInfoEntity denouncedUser;

    @Column(name = "repass_count", nullable = false)
    private Integer repassCount = 0;

    @Column(name = "phase3_notified_at")
    private LocalDateTime phase3NotifiedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<FileEntity> files;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ReportProcessedEntity reportProcessed;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ReportAiAnalysedEntity reportAiAnalysed;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private DefenseEntity defense;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private FinalReportEntity finalReport;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private PreliminaryReportEntity preliminaryReport;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private AppealReportEntity appealReport;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<AppealEntity> appeals;
}
