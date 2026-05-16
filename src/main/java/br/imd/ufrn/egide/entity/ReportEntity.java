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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<FileEntity> files;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ReportProcessedEntity reportProcessed;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private ReportAiAnalysedEntity reportAiAnalysed;
}
