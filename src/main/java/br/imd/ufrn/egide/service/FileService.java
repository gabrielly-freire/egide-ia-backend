package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    void upload(List<MultipartFile> files, ReportEntity report);

    FileEntity findById(Long id);

    Resource findResourceById(Long id);

    List<FileEntity> findAllByReportId(Long reportId);
}
