package com.sentinel.sanction.consumer;

import com.sentinel.sanction.dto.event.ModerationDecisionMadeEvent;
import com.sentinel.sanction.service.SanctionEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationDecisionListener {
    private final ObjectMapper objectMapper;
    private final SanctionEventService eventService;
    @KafkaListener(topics = "${sentinel.kafka.topics.moderation-decision-made}")
    public void consume(String message, Acknowledgment acknowledgment) {
        ModerationDecisionMadeEvent event = objectMapper.readValue(message, ModerationDecisionMadeEvent.class);
        eventService.process(event);
        acknowledgment.acknowledge();
        log.info("Processed moderation decision {} for case {}", event.eventId(), event.aggregateId());
    }
}
