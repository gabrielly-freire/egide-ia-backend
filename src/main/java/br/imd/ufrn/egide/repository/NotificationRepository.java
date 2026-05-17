package br.imd.ufrn.egide.repository;

import br.imd.ufrn.egide.entity.NotificationEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends GenericRepository<NotificationEntity> {

    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndReadAtIsNull(Long recipientId);

    @Modifying
    @Query("""
            update NotificationEntity n
               set n.readAt = :readAt
             where n.recipient.id = :recipientId
               and n.readAt is null
               and n.active = true
            """)
    int markAllAsRead(@Param("recipientId") Long recipientId, @Param("readAt") LocalDateTime readAt);
}
