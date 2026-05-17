package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.AppealRequestDTO;
import br.imd.ufrn.egide.dto.AppealResponseDTO;
import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorCaseDTO;

import java.util.List;

// Interface de serviço para gerenciamento de recursos (Fase 5).
// Permite ao denunciante e ao denunciado abrirem recurso e ao novo ouvidor analisar e emitir relatório de recurso.
public interface AppealService {

    // Submete recurso de uma das partes (denunciante ou denunciado) para a manifestação informada.
    // Regra: cada parte pode abrir apenas um recurso por manifestação.
    AppealResponseDTO submit(Long reportId, AppealRequestDTO request);

    // Retorna todos os recursos abertos para a manifestação informada.
    List<AppealResponseDTO> listByReport(Long reportId);

    // Retorna os casos de recurso atribuídos ao ouvidor autenticado como novo ouvidor (Fase 5).
    List<OuvidorCaseDTO> findAppealCasesAssignedToCurrentOuvidor();

    // Submete o relatório final do recurso emitido pelo novo ouvidor; encerra o ciclo de recurso.
    FinalReportResponseDTO submitAppealReport(Long reportId, FinalReportRequestDTO request);
}
