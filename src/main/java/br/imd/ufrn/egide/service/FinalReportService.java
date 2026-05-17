package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;

// Interface de serviço para gerenciamento do relatório final (Fase 3) emitido pelo ouvidor após a defesa do denunciado.
public interface FinalReportService {

    // Submete o relatório final do ouvidor para a manifestação; avança o status conforme a decisão.
    FinalReportResponseDTO submit(Long reportId, FinalReportRequestDTO request);

    // Retorna o relatório final da manifestação pelo id; lança ResourceNotFoundException se ainda não emitido.
    FinalReportResponseDTO getByReportId(Long reportId);
}
