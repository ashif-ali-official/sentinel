package com.sentinel.sanction.dto.event;

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
    public record Payload(String caseId, String assignmentId, String moderatorId, String reportedPlayerId,
                          String decision, String notes, Instant decidedAt) {}
}
