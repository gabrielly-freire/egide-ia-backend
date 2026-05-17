package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.SatisfactionSurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// Repositório de pesquisas de satisfação; fornece verificação de unicidade e médias para o dashboard.
public interface SatisfactionSurveyRepository extends JpaRepository<SatisfactionSurveyEntity, Long> {

    boolean existsByReportId(Long reportId);

    // Calcula a média das notas de agilidade de todas as pesquisas ativas; retorna null se não houver registros.
    @Query("SELECT AVG(s.speedRating) FROM SatisfactionSurveyEntity s WHERE s.active = true")
    Double getAverageSpeedRating();

    // Calcula a média das notas de resolução de todas as pesquisas ativas; retorna null se não houver registros.
    @Query("SELECT AVG(s.resolutionRating) FROM SatisfactionSurveyEntity s WHERE s.active = true")
    Double getAverageResolutionRating();
}