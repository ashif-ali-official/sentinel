package com.sentinel.appeals.dto.event;
import java.time.Instant;
public record SanctionAppliedEvent(String eventId,String eventType,int eventVersion,String aggregateId,Instant occurredAt,String producer,Payload payload) {
    public record Payload(String caseId,String assignmentId,String playerId,String sanctionType,Long durationSeconds,String actionedBy,Instant appliedAt){}
}
