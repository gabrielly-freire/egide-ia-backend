package br.imd.ufrn.egide.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@Table(name = "file")
@SQLRestriction(value = "active = true")
// Entidade de arquivo de evidência vinculada a uma manifestação ou a um recurso (appeal).
// O campo path armazena o caminho físico no disco (diretório uploads/); nunca exposto na API.
// Um arquivo pode estar vinculado a uma manifestação (report) ou a um recurso (appeal), mas não ambos.
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

    @ManyToOne
    @JoinColumn(name = "appeal_id")
    private AppealEntity appeal;

    @ManyToOne
    @JoinColumn(name = "defense_id")
    private DefenseEntity defense;
}
