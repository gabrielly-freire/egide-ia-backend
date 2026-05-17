package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.FileEntity;

import java.util.List;

// Repositório de arquivos de evidência; herda soft-delete de GenericRepository.
public interface FileRepository extends GenericRepository<FileEntity> {

    List<FileEntity> findAllByReportId(Long reportId);
}
