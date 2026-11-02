package com.sentinel.cases.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReportCreatedEvent (
        String eventId,
        String eventType,
        int eventVersion,
        String aggregateId,
        String correlationId,
        Instant occurredAt,
        String producer,
        ReportPayload payload
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReportPayload (
            String reportId,
            String reporterId,
            String reportedPlayerId,
            String description,
            String category,
            String status
    ) {}
}
