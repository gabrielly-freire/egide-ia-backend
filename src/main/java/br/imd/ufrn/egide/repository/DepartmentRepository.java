package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.DepartmentEntity;
import org.springframework.stereotype.Repository;

// Repositório de departamentos; herda soft-delete e queries filtradas por active de GenericRepository.
@Repository
public interface DepartmentRepository extends GenericRepository<DepartmentEntity> {

}
