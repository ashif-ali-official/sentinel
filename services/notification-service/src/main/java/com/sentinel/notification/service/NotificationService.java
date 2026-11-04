package com.sentinel.notification.service;

import com.sentinel.notification.dto.event.CaseCreatedEvent;
import com.sentinel.notification.dto.response.NotificationResponse;
import com.sentinel.notification.dto.response.PageResponse;
import com.sentinel.notification.entity.Notification;
import com.sentinel.notification.entity.ProcessedEvent;
import com.sentinel.notification.entity.enums.NotificationType;
import com.sentinel.notification.repository.NotificationRepository;
import com.sentinel.notification.repository.ProcessedEventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void processCaseCreated(CaseCreatedEvent event) {

        if (event == null || event.payload() == null
                || event.eventId() == null
                || event.payload().reporterId() == null
                || event.payload().caseId() == null) {
            throw new IllegalArgumentException(
                    "Invalid CASE_CREATED event"
            );
        }

        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Duplicate event, ignored: {}", event.eventId());
            return;
        }

        var payload = event.payload();

        Notification notification = new Notification(
                payload.reporterId(),
                NotificationType.CASE_CREATED,
                "Report recieved",
                "Your report has been received and a moderation case has been created.",
                payload.caseId()
        );

        notificationRepository.save(notification);

        processedEventRepository.save( new ProcessedEvent(
                event.eventId(),
                event.eventType(),
                event.aggregateId()
        ));

        log.info(
                "Notification created for recipient {}",
                payload.reporterId()
        );

    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotifications(
            String recipientId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Notification> notifications =
                notificationRepository
                        .findByRecipientIdOrderByCreatedAtDesc(
                                recipientId,
                                pageable
                        );

        return new PageResponse<>(
                notifications.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList(),
                notifications.getNumber(),
                notifications.getSize(),
                notifications.getTotalElements(),
                notifications.getTotalPages(),
                notifications.isLast()
        );
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String recipientId) {
        return notificationRepository
                .countByRecipientIdAndReadFalse(recipientId);
    }

    @Transactional
    public void markAsRead(String notificationId, String recipientId) {

        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, recipientId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found"
                ));

        notification.markAsRead();
    }

    @Transactional
    public int markAllAsRead(String recipientId) {
        return notificationRepository.markAllAsRead(
                recipientId,
                LocalDateTime.now()
        );
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipientId(),
                notification.getNotificationType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReferenceId(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

}
