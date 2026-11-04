package com.sentinel.notification.consumer;

import com.sentinel.notification.dto.event.CaseCreatedEvent;
import com.sentinel.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaseCreatedEventListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${sentinel.kafka.topics.case-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        try {
            CaseCreatedEvent event = objectMapper.readValue(
                    message,
                    CaseCreatedEvent.class
            );

            notificationService.processCaseCreated(event);

        } catch (Exception e) {
            log.error(
                    "Failed to process CASE_CREATED event: {}",
                    message,
                    e
            );
            throw new RuntimeException(
                    "Failed to process CASE_CREATED event",
                    e
            );
        }
    }

}
