package com.sentinel.appeals.dto.event;
import java.time.Instant;
public record AppealDecidedEvent(String eventId,String eventType,int eventVersion,String aggregateId,Instant occurredAt,String producer,Payload payload) {
    public record Payload(String appealId,String caseId,String playerId,String decision,String decidedBy,String decisionNotes,Instant decidedAt){}
}
