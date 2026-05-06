package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.SatisfactionSurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SatisfactionSurveyRepository extends JpaRepository<SatisfactionSurveyEntity, Long> {
    boolean existsByReportId(Long reportId);

    @Query("SELECT AVG(s.speedRating) FROM SatisfactionSurveyEntity s WHERE s.active = true")
    Double getAverageSpeedRating();

    @Query("SELECT AVG(s.resolutionRating) FROM SatisfactionSurveyEntity s WHERE s.active = true")
    Double getAverageResolutionRating();
}