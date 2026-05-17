package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.AppealRequestDTO;
import br.imd.ufrn.egide.dto.AppealResponseDTO;
import br.imd.ufrn.egide.dto.FinalReportRequestDTO;
import br.imd.ufrn.egide.dto.FinalReportResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorCaseDTO;

import java.util.List;

public interface AppealService {

    AppealResponseDTO submit(Long reportId, AppealRequestDTO request);

    List<AppealResponseDTO> listByReport(Long reportId);

    List<OuvidorCaseDTO> findAppealCasesAssignedToCurrentOuvidor();

    FinalReportResponseDTO submitAppealReport(Long reportId, FinalReportRequestDTO request);
}
