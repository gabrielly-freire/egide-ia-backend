package br.imd.ufrn.egide.dto;

import java.time.LocalDateTime;

// DTO de saída com os dados de uma observação de prova registrada pelo ouvidor.
// Inclui metadados do arquivo (fileId, fileName) para que o cliente exiba a observação
// no contexto correto sem necessidade de chamada adicional ao endpoint de arquivos.
// createdAt e updatedAt são herdados do BaseEntity e úteis para auditoria e histórico.
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
