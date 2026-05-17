package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.AppealStatus;
import br.imd.ufrn.egide.enums.AppellantRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "appeal")
@SQLRestriction(value = "active = true")
public class AppealEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    @ManyToOne
    @JoinColumn(name = "appellant_user_id", nullable = false)
    private UserInfoEntity appellant;

    @Enumerated(EnumType.STRING)
    @Column(name = "appellant_role", nullable = false, length = 32)
    private AppellantRole appellantRole;

    @Column(name = "grounds", columnDefinition = "TEXT")
    private String grounds;

    @ManyToOne
    @JoinColumn(name = "new_ouvidor_id")
    private UserInfoEntity newOuvidor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AppealStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
