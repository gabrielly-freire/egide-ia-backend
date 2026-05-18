package br.imd.ufrn.egide.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "defense")
@SQLRestriction(value = "active = true")
public class DefenseEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String defenseText;

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "submitted_by_user_id", nullable = false)
    private UserInfoEntity submittedBy;

    @OneToOne
    @JoinColumn(name = "report_id", unique = true, nullable = false)
    private ReportEntity report;

    @OneToMany(mappedBy = "defense")
    private List<FileEntity> files;
}
