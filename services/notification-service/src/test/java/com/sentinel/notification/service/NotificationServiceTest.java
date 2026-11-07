package com.sentinel.notification.service;

import com.sentinel.notification.dto.event.CaseCreatedEvent;
import com.sentinel.notification.entity.Notification;
import com.sentinel.notification.repository.NotificationRepository;
import com.sentinel.notification.repository.ProcessedEventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock NotificationRepository notificationRepository;
    @Mock ProcessedEventRepository processedEventRepository;
    @InjectMocks NotificationService service;

    @Test void createsNotificationAndRecordsEventOnce() {
        var event = event("event-1");
        service.processCaseCreated(event);
        verify(notificationRepository).save(any(Notification.class));
        verify(processedEventRepository).save(any());
    }

    @Test void rejectsMalformedAndIgnoresDuplicateEvents() {
        assertThrows(IllegalArgumentException.class, () -> service.processCaseCreated(null));
        var event = event("event-1");
        when(processedEventRepository.existsById("event-1")).thenReturn(true);
        service.processCaseCreated(event);
        verifyNoInteractions(notificationRepository);
        verify(processedEventRepository, never()).save(any());
    }

    @Test void readsNotificationsCountsAndMarksRead() {
        var notification = new Notification("player-1", com.sentinel.notification.entity.enums.NotificationType.CASE_CREATED,
                "Title", "Message", "case-1");
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(eq("player-1"), any()))
                .thenReturn(new PageImpl<>(List.of(notification), PageRequest.of(0, 10), 1));
        when(notificationRepository.countByRecipientIdAndReadFalse("player-1")).thenReturn(2L);
        when(notificationRepository.findByIdAndRecipientId("notification-1", "player-1"))
                .thenReturn(Optional.of(notification));
        assertEquals(1, service.getNotifications("player-1", 0, 10).totalElements());
        assertEquals(2, service.getUnreadCount("player-1"));
        service.markAsRead("notification-1", "player-1");
        when(notificationRepository.markAllAsRead(eq("player-1"), any())).thenReturn(1);
        assertEquals(1, service.markAllAsRead("player-1"));
        verify(notificationRepository).markAllAsRead(eq("player-1"), any());
    }

    @Test void missingNotificationCannotBeMarkedRead() {
        when(notificationRepository.findByIdAndRecipientId("missing", "player-1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.markAsRead("missing", "player-1"));
    }

    private CaseCreatedEvent event(String id) {
        return new CaseCreatedEvent(id, "CASE_CREATED", 1, "case-1", null, Instant.now(), "case-service",
                new CaseCreatedEvent.Payload("case-1", "report-1", "player-1", "player-2", "HARASSMENT", "OPEN"));
    }
}
