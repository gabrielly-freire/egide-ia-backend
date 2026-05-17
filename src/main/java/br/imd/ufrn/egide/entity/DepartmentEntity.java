package br.imd.ufrn.egide.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@Table(name = "department")
@SQLRestriction(value = "active = true")
// Entidade de departamento institucional ao qual os usuários do sistema pertencem.
// O @SQLRestriction garante que apenas registros ativos sejam retornados por padrão (soft-delete herdado de BaseEntity).
public class DepartmentEntity extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String acronym;
}
