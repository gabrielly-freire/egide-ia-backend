package br.imd.ufrn.egide.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@Table(name = "file")
@SQLRestriction(value = "active = true")
public class FileEntity extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;
    private String contentType;
    private Long size;
    @ManyToOne
    @JoinColumn(name = "report_id")
    private ReportEntity report;
}
