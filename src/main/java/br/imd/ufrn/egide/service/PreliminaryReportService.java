package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.PreliminaryReportRequestDTO;
import br.imd.ufrn.egide.dto.PreliminaryReportResponseDTO;
import br.imd.ufrn.egide.dto.ReportResponseSuggestionResponseDTO;

// Interface de serviço para gerenciamento do parecer preliminar (Fase 2) emitido pelo ouvidor.
public interface PreliminaryReportService {

    // Solicita ao serviço de IA uma sugestão de resposta para a manifestação, para auxiliar o ouvidor na elaboração do parecer.
    ReportResponseSuggestionResponseDTO suggestResponse(Long reportId);

    // Submete o parecer preliminar do ouvidor para a manifestação informada; avança o status conforme a decisão.
    PreliminaryReportResponseDTO submit(Long reportId, PreliminaryReportRequestDTO request);

    // Retorna o parecer preliminar da manifestação pelo id; lança ResourceNotFoundException se ainda não emitido.
    PreliminaryReportResponseDTO getByReportId(Long reportId);
}
