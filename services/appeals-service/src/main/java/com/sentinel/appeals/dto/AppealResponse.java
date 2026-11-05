package com.sentinel.appeals.dto;
import com.sentinel.appeals.entity.Appeal;
import java.time.Instant;
public record AppealResponse(String appealId,String caseId,String playerId,String reason,String status,String decisionNotes,
        String decidedBy,Instant submittedAt,Instant decidedAt,Long version){
    public static AppealResponse from(Appeal a){return new AppealResponse(a.getId(),a.getCaseId(),a.getPlayerId(),a.getReason(),a.getStatus(),a.getDecisionNotes(),a.getDecidedBy(),a.getSubmittedAt(),a.getDecidedAt(),a.getVersion());}
}
