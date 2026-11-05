package com.sentinel.moderation.service;

import com.sentinel.moderation.dto.CaseAssignedEvent;
import com.sentinel.moderation.dto.ModerationWorkItemResponse;
import com.sentinel.moderation.dto.RecordDecisionRequest;
import com.sentinel.moderation.dto.event.ModerationDecisionMadeEvent;
import com.sentinel.moderation.entity.ModerationWorkItem;
import com.sentinel.moderation.entity.ModerationOutboxEvent;
import com.sentinel.moderation.entity.ProcessedEvent;
import com.sentinel.moderation.repository.ModerationOutboxRepository;
import com.sentinel.moderation.repository.ModerationWorkItemRepository;
import com.sentinel.moderation.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationWorkService {
    private final ModerationWorkItemRepository workItemRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ModerationOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processAssignment(CaseAssignedEvent event) {
        validate(event);
        if (processedEventRepository.existsById(event.eventId())) return;
        CaseAssignedEvent.Payload payload = event.payload();
        if (!"ASSIGNED".equals(payload.status())) {
            throw new IllegalArgumentException("Unsupported assignment status: " + payload.status());
        }
        if (event.aggregateId() != null && !event.aggregateId().equals(payload.caseId())) {
            throw new IllegalArgumentException("Event aggregate ID does not match case ID");
        }
        ModerationWorkItem existing = workItemRepository.findById(payload.caseId()).orElse(null);
        if (existing != null) {
            if (!existing.getAssignmentId().equals(payload.assignmentId())
                    || !existing.getModeratorId().equals(payload.moderatorId())
                    || !java.util.Objects.equals(existing.getReportedPlayerId(), payload.reportedPlayerId())) {
                throw new IllegalStateException("Case already has a different moderation assignment: " + payload.caseId());
            }
            processedEventRepository.save(new ProcessedEvent(event.eventId(), event.eventType(), payload.caseId()));
            return;
        }
        workItemRepository.save(new ModerationWorkItem(payload.caseId(), payload.assignmentId(), payload.moderatorId(), payload.reportedPlayerId()));
        processedEventRepository.save(new ProcessedEvent(event.eventId(), event.eventType(), payload.caseId()));
    }

    @Transactional(readOnly = true)
    public List<ModerationWorkItemResponse> getQueue(String moderatorId) {
        if (moderatorId == null || moderatorId.isBlank()) throw new IllegalArgumentException("moderatorId is required");
        return workItemRepository.findByModeratorIdOrderByAssignedAtAsc(moderatorId).stream()
                .map(ModerationWorkItemResponse::from).toList();
    }

    @Transactional
    public ModerationWorkItemResponse start(String caseId, String moderatorId) {
        ModerationWorkItem item = workItemRepository.findById(caseId)
                .orElseThrow(() -> new ModerationWorkItemNotFoundException(caseId));
        item.start(moderatorId);
        return ModerationWorkItemResponse.from(workItemRepository.saveAndFlush(item));
    }

    @Transactional
    public ModerationWorkItemResponse recordDecision(String caseId, RecordDecisionRequest request) {
        ModerationWorkItem item = workItemRepository.findById(caseId)
                .orElseThrow(() -> new ModerationWorkItemNotFoundException(caseId));
        if (!item.getVersion().equals(request.expectedVersion())) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
                    com.sentinel.moderation.entity.ModerationWorkItem.class, caseId);
        }

        item.decide(request.moderatorId(), request.decision(), request.notes());
        ModerationWorkItem saved = workItemRepository.saveAndFlush(item);
        Instant decidedAt = saved.getDecidedAt();
        String eventId = UUID.randomUUID().toString();
        ModerationDecisionMadeEvent event = new ModerationDecisionMadeEvent(
                eventId,
                "MODERATION_DECISION_MADE",
                1,
                saved.getCaseId(),
                null,
                decidedAt,
                "moderation-service",
                new ModerationDecisionMadeEvent.Payload(
                        saved.getCaseId(), saved.getAssignmentId(), saved.getModeratorId(),
                        saved.getReportedPlayerId(), saved.getDecision(), saved.getDecisionNotes(), decidedAt));
        try {
            outboxRepository.save(new ModerationOutboxEvent(
                    eventId,
                    saved.getCaseId(),
                    event.eventType(),
                    "sentinel.moderation.decision-made",
                    objectMapper.writeValueAsString(event)));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize moderation decision event", exception);
        }
        return ModerationWorkItemResponse.from(saved);
    }

    private void validate(CaseAssignedEvent event) {
        if (event == null || event.eventId() == null || event.eventType() == null || event.payload() == null) {
            throw new IllegalArgumentException("Invalid CASE_ASSIGNED event");
        }
        if (!"CASE_ASSIGNED".equals(event.eventType()) || event.eventVersion() == null || event.eventVersion() != 1) {
            throw new IllegalArgumentException("Unsupported CASE_ASSIGNED event type or version");
        }
        CaseAssignedEvent.Payload payload = event.payload();
        if (payload.assignmentId() == null || payload.caseId() == null || payload.moderatorId() == null) {
            throw new IllegalArgumentException("CASE_ASSIGNED payload is incomplete");
        }
    }
}
