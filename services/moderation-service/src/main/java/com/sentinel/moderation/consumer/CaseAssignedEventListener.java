package com.sentinel.moderation.consumer;

import com.sentinel.moderation.dto.CaseAssignedEvent;
import com.sentinel.moderation.service.ModerationWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaseAssignedEventListener {
    private final ObjectMapper objectMapper;
    private final ModerationWorkService moderationWorkService;

    @KafkaListener(topics = "${sentinel.kafka.topics.case-assigned}")
    public void consume(String message, Acknowledgment acknowledgment) {
        CaseAssignedEvent event = objectMapper.readValue(message, CaseAssignedEvent.class);
        moderationWorkService.processAssignment(event);
        acknowledgment.acknowledge();
        log.info("Processed CASE_ASSIGNED event {}", event.eventId());
    }
}
