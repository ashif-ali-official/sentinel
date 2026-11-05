package com.sentinel.appeals.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="appeals", uniqueConstraints=@UniqueConstraint(name="uk_appeals_case_id", columnNames="case_id"))
public class Appeal {
    @Id @Column(length=36) private String id;
    @Column(name="case_id", nullable=false, length=36) private String caseId;
    @Column(name="player_id", nullable=false, length=100) private String playerId;
    @Column(nullable=false, length=2000) private String reason;
    @Column(nullable=false, length=30) private String status;
    @Column(name="decision_notes", length=2000) private String decisionNotes;
    @Column(name="decided_by", length=100) private String decidedBy;
    @Column(name="submitted_at", nullable=false) private Instant submittedAt;
    @Column(name="decided_at") private Instant decidedAt;
    @Version @Column(nullable=false) private Long version;
    protected Appeal() {}
    public Appeal(String caseId,String playerId,String reason) { this.id=UUID.randomUUID().toString();this.caseId=caseId;this.playerId=playerId;this.reason=reason;this.status="SUBMITTED";this.submittedAt=Instant.now(); }
    public void decide(boolean accepted,String actor,String notes) {
        if (!"SUBMITTED".equals(status)) throw new IllegalStateException("Appeal is not awaiting a decision");
        status=accepted?"ACCEPTED":"REJECTED"; decidedBy=actor; decisionNotes=notes; decidedAt=Instant.now();
    }
    public String getId(){return id;} public String getCaseId(){return caseId;} public String getPlayerId(){return playerId;} public String getReason(){return reason;}
    public String getStatus(){return status;} public String getDecisionNotes(){return decisionNotes;} public String getDecidedBy(){return decidedBy;}
    public Instant getSubmittedAt(){return submittedAt;} public Instant getDecidedAt(){return decidedAt;} public Long getVersion(){return version;}
}
