package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ProofObservationRequestDTO;
import br.imd.ufrn.egide.dto.ProofObservationResponseDTO;

import java.util.List;

// Interface de serviço para gerenciamento de observações do ouvidor sobre arquivos de prova.
public interface ProofObservationService {

    // Cria ou atualiza (upsert) a observação de um arquivo específico dentro de uma manifestação.
    // Se já existir observação para o par (reportId, fileId), atualiza; senão, cria nova.
    ProofObservationResponseDTO upsert(Long reportId, Long fileId, ProofObservationRequestDTO request);

    // Retorna todas as observações ativas associadas aos arquivos de uma manifestação.
    List<ProofObservationResponseDTO> listByReport(Long reportId);
}
