package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.PreliminaryReportEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório de pareceres preliminares (Fase 2); herda soft-delete de GenericRepository.
@Repository
public interface PreliminaryReportRepository extends GenericRepository<PreliminaryReportEntity> {

    Optional<PreliminaryReportEntity> findByReportId(Long reportId);
}
