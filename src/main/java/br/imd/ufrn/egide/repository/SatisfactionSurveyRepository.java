package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.SatisfactionSurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SatisfactionSurveyRepository extends JpaRepository<SatisfactionSurveyEntity, Long> {
    boolean existsByReportId(Long reportId);
}