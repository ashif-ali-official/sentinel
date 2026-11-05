package com.sentinel.sanction.dto.event;

import com.sentinel.sanction.entity.enums.SanctionType;
import java.time.Instant;

public record SanctionAppliedEvent(String eventId, String eventType, int eventVersion, String aggregateId,
        Instant occurredAt, String producer, Payload payload) {
    public record Payload(String caseId, String assignmentId, String playerId, SanctionType sanctionType,
            Long durationSeconds, String actionedBy, Instant appliedAt) {}
}
