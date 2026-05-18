package br.imd.ufrn.egide.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO de saída principal da manifestação; retornado nos endpoints de listagem e consulta.
// Para usuários com papel MANAGER, o título e a descrição podem ser substituídos pelas versões
// anonimizadas quando há conflito de interesse detectado pela IA (ver ReportServiceImpl.toDTOForViewer).
public record ReportDTO(
        Long id,

        String protocolNumber,

        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        LocalDate dateOfOccurrence,

        Long userInfoId,

        String status,

        Long ouvidorId,

        String ouvidorName,

        LocalDateTime createdAt
) {
}
