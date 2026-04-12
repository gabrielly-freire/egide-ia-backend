package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.dto.ReportRequestDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {

    ReportDTO save(ReportRequestDTO reportRequestDTO, List<MultipartFile> files);

    List<ReportDTO> findAll();

    ReportDTO getById(Long id);

    ReportEntity findEntityById(Long id);
}
