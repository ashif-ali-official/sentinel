package com.sentinel.appeals.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "appealable_sanctions")
public class AppealableSanction {
    @Id @Column(name = "case_id", length = 36) private String caseId;
    @Column(name = "sanction_event_id", nullable = false, unique = true, length = 36) private String sanctionEventId;
    @Column(name = "assignment_id", nullable = false, length = 36) private String assignmentId;
    @Column(name = "player_id", nullable = false, length = 100) private String playerId;
    @Column(name = "sanction_type", nullable = false, length = 40) private String sanctionType;
    @Column(name = "duration_seconds") private Long durationSeconds;
    @Column(nullable = false, length = 30) private String status;
    @Column(name = "applied_at", nullable = false) private Instant appliedAt;
    @Version @Column(nullable = false) private Long version;
    protected AppealableSanction() {}
    public AppealableSanction(String caseId,String eventId,String assignmentId,String playerId,String type,Long duration,Instant appliedAt) {
        this.caseId=caseId; this.sanctionEventId=eventId; this.assignmentId=assignmentId; this.playerId=playerId;
        this.sanctionType=type; this.durationSeconds=duration; this.appliedAt=appliedAt; this.status="AVAILABLE";
    }
    public void markAppealed() { if (!"AVAILABLE".equals(status)) throw new IllegalStateException("Sanction is not available for appeal"); status="UNDER_APPEAL"; }
    public void decide(boolean accepted) { if (!"UNDER_APPEAL".equals(status)) throw new IllegalStateException("Sanction has no pending appeal"); status=accepted?"REVERSED":"APPEAL_REJECTED"; }
    public String getCaseId(){return caseId;} public String getSanctionEventId(){return sanctionEventId;} public String getAssignmentId(){return assignmentId;}
    public String getPlayerId(){return playerId;} public String getSanctionType(){return sanctionType;} public Long getDurationSeconds(){return durationSeconds;}
    public String getStatus(){return status;} public Instant getAppliedAt(){return appliedAt;} public Long getVersion(){return version;}
}
