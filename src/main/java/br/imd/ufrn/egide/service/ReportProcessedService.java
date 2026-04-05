package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportProcessedDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportProcessedService {

    ReportProcessedDTO save(ReportProcessedDTO dto);

    ReportProcessedDTO update(Long id, ReportProcessedDTO dto);

    void delete(Long id);

    ReportProcessedDTO get(Long id);

    ReportProcessedDTO getByReportId(Long reportId);

    Page<ReportProcessedDTO> list(Pageable pageable);
}
