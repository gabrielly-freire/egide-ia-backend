package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.AppealReportEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppealReportRepository extends GenericRepository<AppealReportEntity> {

    Optional<AppealReportEntity> findByReportId(Long reportId);
}
