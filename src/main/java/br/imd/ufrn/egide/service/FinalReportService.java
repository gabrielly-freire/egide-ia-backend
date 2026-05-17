package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;

public interface FinalReportService {

    FinalReportResponseDTO submit(Long reportId, FinalReportRequestDTO request);

    FinalReportResponseDTO getByReportId(Long reportId);
}
