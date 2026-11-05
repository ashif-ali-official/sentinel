package com.sentinel.assignment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.assignment.dto.request.CreateAssignmentRequest;
import com.sentinel.assignment.entity.AssignmentOutboxEvent;
import com.sentinel.assignment.entity.CaseAssignment;
import com.sentinel.assignment.entity.ProcessedEvent;
import com.sentinel.assignment.kafka.event.CaseAssignedEvent;
import com.sentinel.assignment.kafka.event.CaseAssignedPayload;
import com.sentinel.assignment.kafka.event.CaseCreatedEvent;
import com.sentinel.assignment.repository.AssignmentOutboxRepository;
import com.sentinel.assignment.repository.CaseAssignmentRepository;
import com.sentinel.assignment.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentEventProcessor {

    private final ProcessedEventRepository processedEventRepository;
    private final CaseAssignmentRepository assignmentRepository;
    private final AssignmentService assignmentService;
    private final AssignmentOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void process(CaseCreatedEvent event, String region) {

        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Event already processed: {}", event.eventId());
            return;
        }

        String caseId = event.payload().caseId();

        if (!assignmentRepository.existsByCaseId(caseId)) {

            CreateAssignmentRequest request =
                    new CreateAssignmentRequest(caseId, region);

            CaseAssignment assignment =
                    assignmentService.createAssignment(request);

            saveAssignmentEvent(assignment, event);
        }

        saveProcessedEvent(event);

        log.info("Successfully processed CASE_CREATED. caseId={}", caseId);
    }

    private void saveProcessedEvent(CaseCreatedEvent event) {

        ProcessedEvent processedEvent = new ProcessedEvent();

        processedEvent.setEventId(event.eventId());
        processedEvent.setEventType(event.eventType());
        processedEvent.setAggregateId(event.aggregateId());
        processedEvent.setProcessedAt(LocalDateTime.now());

        processedEventRepository.save(processedEvent);
    }

    private void saveAssignmentEvent(
            CaseAssignment assignment,
            CaseCreatedEvent sourceEvent) {

        try {
            CaseAssignedEvent event = new CaseAssignedEvent(
                    UUID.randomUUID().toString(),
                    "CASE_ASSIGNED",
                    1,
                    assignment.getCaseId(),
                    sourceEvent.correlationId(),
                    LocalDateTime.now(),
                    "assignment-service",
                    new CaseAssignedPayload(
                            assignment.getId(),
                            assignment.getCaseId(),
                            assignment.getModeratorId(),
                            assignment.getStatus().name(),
                            sourceEvent.payload().reportedPlayerId()
                    )
            );

            AssignmentOutboxEvent outbox = new AssignmentOutboxEvent();

            outbox.setId(event.eventId());
            outbox.setAggregateType("CASE");
            outbox.setAggregateId(assignment.getCaseId());
            outbox.setEventType("CASE_ASSIGNED");
            outbox.setTopic("sentinel.case.assigned");
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setStatus("PENDING");
            outbox.setAttempts(0);
            outbox.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(outbox);

        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Failed to serialize CASE_ASSIGNED event", ex);
        }
    }

}
