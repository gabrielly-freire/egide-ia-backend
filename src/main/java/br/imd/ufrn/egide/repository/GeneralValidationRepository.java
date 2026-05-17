package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.GeneralValidationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositório de validações do Ouvidor Geral (Fase 4); herda soft-delete de GenericRepository.
@Repository
public interface GeneralValidationRepository extends GenericRepository<GeneralValidationEntity> {

    List<GeneralValidationEntity> findAllByReportIdOrderByIdAsc(Long reportId);
}
