package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.ProofObservationRequestDTO;
import br.imd.ufrn.egide.dto.ProofObservationResponseDTO;

import java.util.List;

public interface ProofObservationService {

    ProofObservationResponseDTO upsert(Long reportId, Long fileId, ProofObservationRequestDTO request);

    List<ProofObservationResponseDTO> listByReport(Long reportId);
}
