package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.ReportAiAnalysedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório de resultados de análise de IA; estende JpaRepository diretamente pois
// não utiliza soft-delete próprio (herda o filtro via @SQLRestriction na entidade).
@Repository
public interface ReportAiAnalysedRepository extends JpaRepository<ReportAiAnalysedEntity, Long> {

    Optional<ReportAiAnalysedEntity> findByReportId(Long reportId);
}
