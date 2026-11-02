package com.sentinel.report.event;

import java.time.Instant;
import java.util.UUID;

public record ReportCreatedEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        String aggregateId,
        String correlationId,
        Instant occurredAt,
        String producer,
        ReportPayload payload
) {
    public record ReportPayload(
            String reportId,
            String reporterId,
            String reportedPlayerId,
            String category,
            String status
    ) {}
}
