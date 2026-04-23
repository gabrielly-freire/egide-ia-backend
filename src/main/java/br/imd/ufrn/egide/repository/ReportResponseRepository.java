package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.ReportResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportResponseRepository extends GenericRepository<ReportResponseEntity> {

    Optional<ReportResponseEntity> findByReportId(Long reportId);
}
