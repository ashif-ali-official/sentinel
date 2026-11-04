package com.sentinel.assignment.kafka.event;

import java.time.LocalDateTime;

public record CaseCreatedEvent(
        String eventId,
        String eventType,
        Integer eventVersion,
        String aggregateId,
        String correlationId,
        LocalDateTime occurredAt,
        String producer,
        CaseCreatedPayload payload
) {}