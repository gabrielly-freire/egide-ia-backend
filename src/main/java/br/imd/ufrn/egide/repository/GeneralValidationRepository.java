package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.GeneralValidationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralValidationRepository extends GenericRepository<GeneralValidationEntity> {

    List<GeneralValidationEntity> findAllByReportIdOrderByIdAsc(Long reportId);
}
