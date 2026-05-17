package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.GeneralValidationAlterRequestDTO;
import br.imd.ufrn.egide.dto.GeneralValidationResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorGeralCaseDTO;

import java.util.List;

public interface GeneralValidationService {

    List<OuvidorGeralCaseDTO> findPendingCases();

    GeneralValidationResponseDTO validate(Long reportId);

    GeneralValidationResponseDTO alter(Long reportId, GeneralValidationAlterRequestDTO request);

    GeneralValidationResponseDTO repass(Long reportId);
}
