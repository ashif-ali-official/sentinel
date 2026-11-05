package com.sentinel.cases.dto.event;

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
) {
    public record CaseAssignedPayload(
            String assignmentId,
            String caseId,
            String moderatorId,
            String status,
            String reportedPlayerId
    ) {
    }
}
