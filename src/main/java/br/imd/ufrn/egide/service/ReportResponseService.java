package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportRespondRequestDTO;
import br.imd.ufrn.egide.dto.ReportRespondResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;

public interface ReportResponseService {

    ReportResponseSuggestionResponseDTO suggestResponse(Long reportId);

    ReportRespondResponseDTO respond(Long reportId, ReportRespondRequestDTO request);

    ReportRespondResponseDTO getResponse(Long reportId);
}
