package com.sentinel.assignment.service;

import com.sentinel.assignment.dto.request.CreateAssignmentRequest;
import com.sentinel.assignment.entity.ProcessedEvent;
import com.sentinel.assignment.kafka.event.CaseCreatedEvent;
import com.sentinel.assignment.repository.CaseAssignmentRepository;
import com.sentinel.assignment.repository.ProcessedEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentEventProcessor {

    private final ProcessedEventRepository processedEventRepository;
    private final CaseAssignmentRepository assignmentRepository;
    private final AssignmentService assignmentService;

    @Transactional
    public void process(CaseCreatedEvent event, String region) {

        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Event already processed: {}", event.eventId());
            return;
        }

        String caseId = event.payload().caseId();

        // Protect against a case already assigned through another path.
        if (!assignmentRepository.existsByCaseId(caseId)) {

            CreateAssignmentRequest request =
                    new CreateAssignmentRequest(caseId, region);

            assignmentService.assignCase(request);
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
}