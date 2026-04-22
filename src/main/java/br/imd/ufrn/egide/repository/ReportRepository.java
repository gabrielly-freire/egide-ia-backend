package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends GenericRepository<ReportEntity> {
    List<ReportEntity> findAllByStatusAndCreatedAtBefore(ReportStatus status, LocalDateTime dateTime);
    List<ReportEntity> findAllByStatusAndCreatedAtBetween(ReportStatus status, LocalDateTime start, LocalDateTime end);
}