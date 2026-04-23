package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ReportAnalysedRequestDTO;
import br.imd.ufrn.egide.dto.ReportAnalysedResponseDTO;
import br.imd.ufrn.egide.dto.ReportAnonymizedRequestDTO;
import br.imd.ufrn.egide.dto.ReportAnonymizedResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionRequestDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;

public interface ReportAiService {

    void processReport(Long reportId);

    ReportAnonymizedResponseDTO anonymize(ReportAnonymizedRequestDTO report);

    ReportAnalysedResponseDTO classify(ReportAnalysedRequestDTO report);

    ReportResponseSuggestionResponseDTO suggestResponse(ReportResponseSuggestionRequestDTO report);
}
