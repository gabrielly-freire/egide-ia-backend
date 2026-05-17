package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.AppealReportEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório de relatórios de recurso (Fase 5); herda soft-delete de GenericRepository.
@Repository
public interface AppealReportRepository extends GenericRepository<AppealReportEntity> {

    Optional<AppealReportEntity> findByReportId(Long reportId);
}
