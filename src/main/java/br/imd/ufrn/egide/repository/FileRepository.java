package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.FileEntity;

import java.util.List;

public interface FileRepository extends GenericRepository<FileEntity> {

    List<FileEntity> findAllByReportId(Long reportId);
}
