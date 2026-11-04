package com.sentinel.notification.dto.response;

import com.sentinel.notification.entity.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        String id,
        String recipientId,
        NotificationType notificationType,
        String title,
        String message,
        String referenceId,
        boolean read,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {}