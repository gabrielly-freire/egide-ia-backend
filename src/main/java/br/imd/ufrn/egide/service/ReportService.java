package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportDTO;
import br.imd.ufrn.egide.enums.ReportCategory;

public interface ReportService {

    ReportDTO save(ReportDTO reportDTO);

    ReportDTO getById(Long id);

    ReportDTO updateCategory(Long id, ReportCategory category);
}
