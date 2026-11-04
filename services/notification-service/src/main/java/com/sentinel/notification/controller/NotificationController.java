package com.sentinel.notification.controller;

import com.sentinel.notification.dto.response.NotificationResponse;
import com.sentinel.notification.dto.response.PageResponse;
import com.sentinel.notification.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>>
    getNotifications(
            @RequestParam String recipientId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(
                notificationService.getNotifications(
                        recipientId, page, size
                )
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @RequestParam String recipientId
    ) {
        return ResponseEntity.ok(
                notificationService.getUnreadCount(recipientId)
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String notificationId,
            @RequestParam String recipientId
    ) {
        notificationService.markAsRead(notificationId, recipientId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Integer> markAllAsRead(
            @RequestParam String recipientId
    ) {
        return ResponseEntity.ok(
                notificationService.markAllAsRead(recipientId)
        );
    }
}