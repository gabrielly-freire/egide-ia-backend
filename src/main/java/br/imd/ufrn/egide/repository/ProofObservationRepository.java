package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.ProofObservationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProofObservationRepository extends GenericRepository<ProofObservationEntity> {

    @Query("select po from ProofObservationEntity po " +
           "where po.file.id = :fileId " +
           "  and po.file.report.id = :reportId " +
           "  and po.active = true")
    Optional<ProofObservationEntity> findByReportAndFile(@Param("reportId") Long reportId,
                                                        @Param("fileId") Long fileId);

    @Query("select po from ProofObservationEntity po " +
           "where po.file.report.id = :reportId " +
           "  and po.active = true")
    List<ProofObservationEntity> findAllByReport(@Param("reportId") Long reportId);
}
