package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.FinalReportEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório de relatórios finais (Fase 3); herda soft-delete de GenericRepository.
@Repository
public interface FinalReportRepository extends GenericRepository<FinalReportEntity> {

    Optional<FinalReportEntity> findByReportId(Long reportId);
}
