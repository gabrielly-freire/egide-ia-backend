package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.ProofObservationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositório de observações de prova (anotações do ouvidor sobre arquivos de evidência).
// Herda soft-delete de GenericRepository; as queries filtram explicitamente por active = true
// pois navegam por associações (file.report) que o @SQLRestriction não cobre transitivamente.
@Repository
public interface ProofObservationRepository extends GenericRepository<ProofObservationEntity> {

    // Busca a observação de uma prova específica (arquivo) dentro de uma manifestação.
    // Usada para lógica de upsert: se existir, atualiza; senão, cria nova observação.
    @Query("select po from ProofObservationEntity po " +
           "where po.file.id = :fileId " +
           "  and po.file.report.id = :reportId " +
           "  and po.active = true")
    Optional<ProofObservationEntity> findByReportAndFile(@Param("reportId") Long reportId,
                                                        @Param("fileId") Long fileId);

    // Retorna todas as observações ativas de todos os arquivos de uma manifestação.
    @Query("select po from ProofObservationEntity po " +
           "where po.file.report.id = :reportId " +
           "  and po.active = true")
    List<ProofObservationEntity> findAllByReport(@Param("reportId") Long reportId);
}
