package com.sentinel.notification.entity;

import com.sentinel.notification.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    private String id;

    @Column(name = "recipient_id", nullable = false)
    private String recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public Notification(
            String recipientId,
            NotificationType notificationType,
            String title,
            String message,
            String referenceId
    ) {
        this.id = UUID.randomUUID().toString();
        this.recipientId = recipientId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.referenceId = referenceId;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        if(!this.read) {
            this.read = true;
            this.readAt = LocalDateTime.now();
        }
    }

}
