package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.NotificationResponseDTO;
import java.util.List;

public interface NotificationService {
    void notifyDenouncedPhase3Started(Long reportId, Long denouncedUserId);

    List<NotificationResponseDTO> listMyNotifications();

    long getUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();

    public void notifySlaExpired(Long reportId, Long recipientId);

    void notifyOuvidorAssigned(Long reportId, Long ouvidorId);

    void notifyDenunciantePreliminaryIssued(Long reportId, Long denuncianteId);

    void notifyOuvidorDefenseSubmitted(Long reportId, Long ouvidorId);
}
