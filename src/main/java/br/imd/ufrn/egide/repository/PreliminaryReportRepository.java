package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.PreliminaryReportEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreliminaryReportRepository extends GenericRepository<PreliminaryReportEntity> {

    Optional<PreliminaryReportEntity> findByReportId(Long reportId);
}
