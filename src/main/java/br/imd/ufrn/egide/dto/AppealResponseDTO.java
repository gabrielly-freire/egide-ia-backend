package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.AppealStatus;
import br.imd.ufrn.egide.enums.AppellantRole;

import java.time.LocalDateTime;

// DTO de saída com os dados de um recurso individual (Fase 5).
// Retornado após submissão e nas consultas de listagem de recursos por manifestação.
// Destaques:
//   - newOuvidorId / newOuvidorName: ouvidor designado para análise do recurso;
//     se houver dois recursos (regra de merge), ambos compartilharão o mesmo ouvidor.
//   - closedAt: nulo enquanto o recurso estiver em aberto; preenchido quando a OG encerra o caso.
//   - status: permite que o cliente rastreie a progressão do recurso individualmente.
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
