package com.sentinel.assignment.kafka.event;

import java.time.LocalDateTime;

public record CaseAssignedEvent(
        String eventId,
        String eventType,
        Integer eventVersion,
        String aggregateId,
        String correlationId,
        LocalDateTime occurredAt,
        String producer,
        CaseAssignedPayload payload
) {}