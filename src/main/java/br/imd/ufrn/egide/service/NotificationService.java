package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.NotificationResponseDTO;
import java.util.List;

public interface NotificationService {
    void notifyDenouncedPhase3Started(Long reportId, Long denouncedUserId);

    List<NotificationResponseDTO> listMyNotifications();

    long getUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();
}
