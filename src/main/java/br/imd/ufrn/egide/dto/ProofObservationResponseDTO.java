package br.imd.ufrn.egide.dto;

import java.time.LocalDateTime;

public record ProofObservationResponseDTO(
        Long id,
        Long fileId,
        String fileName,
        Long ouvidorId,
        String observation,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
