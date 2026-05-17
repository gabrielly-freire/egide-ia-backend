package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.ReportProcessedEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório de entidades de triagem pós-IA; herda soft-delete de GenericRepository.
@Repository
public interface ReportProcessedRepository extends GenericRepository<ReportProcessedEntity> {

    Optional<ReportProcessedEntity> findByReportId(Long reportId);
}
