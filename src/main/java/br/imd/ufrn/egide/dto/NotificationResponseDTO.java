package br.imd.ufrn.egide.dto;

import br.imd.ufrn.egide.enums.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponseDTO(
        Long id,
        NotificationType type,
        String title,
        String message,
        LocalDateTime createdAt,
        LocalDateTime readAt,
        Long reportId
) {
}
