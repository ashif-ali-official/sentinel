package com.sentinel.cases.consumer;

import com.sentinel.cases.dto.event.CaseAssignedEvent;
import com.sentinel.cases.service.CaseEventConsumerService;
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
    private final CaseEventConsumerService caseEventConsumerService;

    @KafkaListener(topics = "${sentinel.kafka.topics.case-assigned}")
    public void consume(String message, Acknowledgment acknowledgment) {
        CaseAssignedEvent event = objectMapper.readValue(message, CaseAssignedEvent.class);
        caseEventConsumerService.processCaseAssigned(event);
        acknowledgment.acknowledge();
        log.info("Successfully processed CaseAssigned event: {}", event.eventId());
    }
}
