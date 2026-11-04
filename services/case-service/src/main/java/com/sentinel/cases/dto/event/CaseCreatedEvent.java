package com.sentinel.cases.dto.event;

import java.time.Instant;

public record CaseCreatedEvent(
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
            String reportId,
            String reporterId,
            String reportedPlayerId,
            String category,
            String status
    ) {}
}
