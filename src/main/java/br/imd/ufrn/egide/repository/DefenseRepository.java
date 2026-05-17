package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.DefenseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DefenseRepository extends GenericRepository<DefenseEntity> {
    Optional<DefenseEntity> findByReportId(Long reportId);
}
