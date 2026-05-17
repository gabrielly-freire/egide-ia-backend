package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.AppealEntity;
import br.imd.ufrn.egide.enums.AppellantRole;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositório de recursos (appeals) da Fase 5; herda soft-delete de GenericRepository.
@Repository
public interface AppealRepository extends GenericRepository<AppealEntity> {

    List<AppealEntity> findAllByReportId(Long reportId);

    Optional<AppealEntity> findByReportIdAndAppellantRole(Long reportId, AppellantRole appellantRole);

    List<AppealEntity> findAllByNewOuvidorId(Long newOuvidorId);
}
