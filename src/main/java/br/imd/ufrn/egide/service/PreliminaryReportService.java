package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.PreliminaryReportRequestDTO;
import br.imd.ufrn.egide.dto.PreliminaryReportResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;

public interface PreliminaryReportService {

    ReportResponseSuggestionResponseDTO suggestResponse(Long reportId);

    PreliminaryReportResponseDTO submit(Long reportId, PreliminaryReportRequestDTO request);

    PreliminaryReportResponseDTO getByReportId(Long reportId);
}
