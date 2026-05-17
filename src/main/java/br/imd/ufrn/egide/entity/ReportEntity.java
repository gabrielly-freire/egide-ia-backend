package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.List;

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
    private UserInfoEntity denunciadoUser;

    @Column(name = "repass_count", nullable = false)
    private Integer repassCount = 0;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<FileEntity> files;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ReportProcessedEntity reportProcessed;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ReportAiAnalysedEntity reportAiAnalysed;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private PreliminaryReportEntity preliminaryReport;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private FinalReportEntity finalReport;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private AppealReportEntity appealReport;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<AppealEntity> appeals;
}
