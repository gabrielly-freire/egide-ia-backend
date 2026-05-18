package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.enums.ReportStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Repositório central de manifestações; herda soft-delete de GenericRepository.
// Fornece consultas para SLA, dashboard, visão do ouvidor e sorteio de designação.
@Repository
public interface ReportRepository extends GenericRepository<ReportEntity> {

    List<ReportEntity> findAllByStatusAndCreatedAtBefore(ReportStatus status, LocalDateTime dateTime);

    List<ReportEntity> findAllByStatusAndCreatedAtBetween(ReportStatus status, LocalDateTime start, LocalDateTime end);

    List<ReportEntity> findByUserInfoId(Long userInfoId);

    List<ReportEntity> findByDenouncedUserIdAndStatusIn(Long denouncedUserId, List<ReportStatus> statuses);

    long countByStatus(ReportStatus status);

    List<ReportEntity> findByOuvidorId(Long ouvidorId);

    // Conta os casos ativos (não-encerrados) de um ouvidor; utilizado pelo OuvidorAssignmentService
    // para identificar os 3 ouvidores com menor carga antes do sorteio.
    @Query("select count(r) from ReportEntity r " +
           "where r.ouvidor.id = :ouvidorId " +
           "  and r.active = true " +
           "  and r.status not in :closedStatuses")
    long countActiveCasesForOuvidor(@Param("ouvidorId") Long ouvidorId,
                                    @Param("closedStatuses") List<ReportStatus> closedStatuses);

    List<ReportEntity> findByStatusIn(List<ReportStatus> statuses);
}