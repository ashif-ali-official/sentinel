package com.sentinel.notification.repository;

import com.sentinel.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, String> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(
            String recipientId,
            Pageable pageable
    );

    long countByRecipientIdAndReadFalse(String recipientId);

    Optional<Notification> findByIdAndRecipientId(
            String id,
            String recipientId
    );

    @Modifying
    @Query("""
    UPDATE Notification n
    SET n.read = true, n.readAt = :readAt
    WHERE n.recipientId = :recipientId
      AND n.read = false
""")
    int markAllAsRead(
            @Param("recipientId") String recipientId,
            @Param("readAt") LocalDateTime readAt
    );
}