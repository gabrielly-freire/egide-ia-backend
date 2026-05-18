package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@Table(name = "notification")
@SQLRestriction(value = "active = true")
public class NotificationEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private UserInfoEntity recipient;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private ReportEntity report;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
