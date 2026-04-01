package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;
import java.util.List;

@Data
@Entity
@Table(name = "report")
@SQLRestriction(value = "active = true")
public class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<FileEntity> files;
}