package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.dto.ReportRequestDTO;
import br.imd.ufrn.egide.dto.SatisfactionSurveyRequestDTO;
import br.imd.ufrn.egide.entity.ReportEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReportService {

    ReportDTO save(ReportRequestDTO reportRequestDTO, List<MultipartFile> files);

    List<ReportDTO> findAll();

    List<ReportDTO> findMyReports();

    ReportDTO getById(Long id);

    ReportEntity findEntityById(Long id);

    Map<String, Object> getDashboardStatus();

    void saveSurvey(Long reportId, SatisfactionSurveyRequestDTO dto);
}
