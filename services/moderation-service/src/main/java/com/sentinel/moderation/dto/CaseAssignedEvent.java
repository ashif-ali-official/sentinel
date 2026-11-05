package com.sentinel.moderation.dto;

import java.time.LocalDateTime;

public record CaseAssignedEvent(
        String eventId,
        String eventType,
        Integer eventVersion,
        String aggregateId,
        String correlationId,
        LocalDateTime occurredAt,
        String producer,
        Payload payload
) {
    public record Payload(String assignmentId, String caseId, String moderatorId, String status, String reportedPlayerId) {}
}
