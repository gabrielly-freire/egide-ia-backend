package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.AppealStatus;
import br.imd.ufrn.egide.enums.AppellantRole;

import java.time.LocalDateTime;

public record AppealResponseDTO(
        Long id,
        Long reportId,
        Long appellantUserId,
        String appellantName,
        AppellantRole appellantRole,
        String grounds,
        Long newOuvidorId,
        String newOuvidorName,
        AppealStatus status,
        LocalDateTime submittedAt,
        LocalDateTime closedAt
) {
}
