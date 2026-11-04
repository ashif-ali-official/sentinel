package com.sentinel.notification.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    public record Payload (
            String caseId,
            String reportId,
            String reporterId,
            String reportedPlayerId,
            String category,
            String status
    ) {}
}
