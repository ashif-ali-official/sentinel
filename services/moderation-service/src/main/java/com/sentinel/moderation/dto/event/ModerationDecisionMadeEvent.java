package com.sentinel.moderation.dto.event;

import com.sentinel.moderation.entity.enums.ModerationDecision;

import java.time.Instant;

public record ModerationDecisionMadeEvent(
        String eventId,
        String eventType,
        int eventVersion,
        String aggregateId,
        String correlationId,
        Instant occurredAt,
        String producer,
        Payload payload
) {
    public record Payload(
            String caseId,
            String assignmentId,
            String moderatorId,
            String reportedPlayerId,
            ModerationDecision decision,
            String notes,
            Instant decidedAt
    ) {}
}
