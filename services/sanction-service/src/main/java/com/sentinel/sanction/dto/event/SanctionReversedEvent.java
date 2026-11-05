package com.sentinel.sanction.dto.event;
import com.sentinel.sanction.entity.enums.SanctionType;
import java.time.Instant;
public record SanctionReversedEvent(String eventId,String eventType,int eventVersion,String aggregateId,Instant occurredAt,String producer,Payload payload){
    public record Payload(String caseId,String playerId,SanctionType sanctionType,String reason,String reversedBy,Instant reversedAt){}
}
