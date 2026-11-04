package com.sentinel.assignment.kafka.consumer;

import com.sentinel.assignment.kafka.event.CaseCreatedEvent;
import com.sentinel.assignment.service.AssignmentEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaseCreatedConsumer {

    private final AssignmentEventService assignmentEventService;

    @KafkaListener(
            topics = "sentinel.case.created",
            groupId = "sentinel-assignment-service"
    )
    public void consume(CaseCreatedEvent event) {

        log.info(
                "Received CASE_CREATED event. eventId={}, caseId={}",
                event.eventId(),
                event.payload().caseId()
        );

        assignmentEventService.processCaseCreated(event);
    }
}