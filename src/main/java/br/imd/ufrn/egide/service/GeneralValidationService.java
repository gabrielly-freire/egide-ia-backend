package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.GeneralValidationAlterRequestDTO;
import br.imd.ufrn.egide.dto.GeneralValidationResponseDTO;
import br.imd.ufrn.egide.dto.OuvidorGeralCaseDTO;

import java.util.List;

// Interface de serviço para as ações do Ouvidor Geral na Fase 4 (validação, alteração e repasse).
public interface GeneralValidationService {

    // Retorna os casos aguardando validação do Ouvidor Geral (status FINAL_ISSUED).
    List<OuvidorGeralCaseDTO> findPendingCases();

    // Valida o relatório final do ouvidor sem alterações; avança o status para GENERAL_VALIDATED.
    GeneralValidationResponseDTO validate(Long reportId);

    // Altera o conteúdo do relatório final antes de validar; registra a ação com tipo ALTER.
    GeneralValidationResponseDTO alter(Long reportId, GeneralValidationAlterRequestDTO request);

    // Repassa o caso para um novo ouvidor; limitado a 1 repass por manifestação (repassCount <= 1).
    // Atualiza o ouvidor designado e muda o status para REPASSED.
    GeneralValidationResponseDTO repass(Long reportId);
}
