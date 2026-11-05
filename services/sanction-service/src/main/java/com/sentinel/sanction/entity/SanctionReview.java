package com.sentinel.sanction.entity;

import com.sentinel.sanction.entity.enums.SanctionStatus;
import com.sentinel.sanction.entity.enums.SanctionType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "sanction_reviews")
public class SanctionReview {
    @Id
    @Column(name = "case_id", length = 36)
    private String caseId;
    @Column(name = "assignment_id", nullable = false, length = 36)
    private String assignmentId;
    @Column(name = "reported_player_id", nullable = false, length = 100)
    private String reportedPlayerId;
    @Column(name = "moderator_id", nullable = false, length = 100)
    private String moderatorId;
    @Column(name = "moderation_decision", nullable = false, length = 40)
    private String moderationDecision;
    @Column(name = "decision_notes", length = 2000)
    private String decisionNotes;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SanctionStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "sanction_type", length = 40)
    private SanctionType sanctionType;
    @Column(name = "duration_seconds")
    private Long durationSeconds;
    @Column(name = "actioned_by", length = 100)
    private String actionedBy;
    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;
    @Column(name = "actioned_at")
    private Instant actionedAt;
    @Version
    @Column(nullable = false)
    private Long version;

    protected SanctionReview() {}

    public SanctionReview(String caseId, String assignmentId, String playerId, String moderatorId,
                          String decision, String notes, Instant decidedAt) {
        this.caseId = caseId;
        this.assignmentId = assignmentId;
        this.reportedPlayerId = playerId;
        this.moderatorId = moderatorId;
        this.moderationDecision = decision;
        this.decisionNotes = notes;
        this.decidedAt = decidedAt;
        this.status = "VIOLATION_CONFIRMED".equals(decision) ? SanctionStatus.ACTION_REQUIRED : SanctionStatus.NO_ACTION;
    }

    public void apply(SanctionType type, Long durationSeconds, String actionedBy) {
        if (status != SanctionStatus.ACTION_REQUIRED) throw new IllegalStateException("Sanction action is not required for this decision");
        if (type == null) throw new IllegalArgumentException("Sanction type is required");
        if (type == SanctionType.TEMPORARY_SUSPENSION && (durationSeconds == null || durationSeconds < 60)) {
            throw new IllegalArgumentException("Temporary suspension duration must be at least 60 seconds");
        }
        if (type != SanctionType.TEMPORARY_SUSPENSION && durationSeconds != null) {
            throw new IllegalArgumentException("Duration is only valid for temporary suspensions");
        }
        this.sanctionType = type;
        this.durationSeconds = durationSeconds;
        this.actionedBy = actionedBy;
        this.actionedAt = Instant.now();
        this.status = SanctionStatus.APPLIED;
    }

    public String getCaseId() { return caseId; }
    public String getAssignmentId() { return assignmentId; }
    public String getReportedPlayerId() { return reportedPlayerId; }
    public String getModeratorId() { return moderatorId; }
    public String getModerationDecision() { return moderationDecision; }
    public String getDecisionNotes() { return decisionNotes; }
    public SanctionStatus getStatus() { return status; }
    public SanctionType getSanctionType() { return sanctionType; }
    public Long getDurationSeconds() { return durationSeconds; }
    public String getActionedBy() { return actionedBy; }
    public Instant getDecidedAt() { return decidedAt; }
    public Instant getActionedAt() { return actionedAt; }
    public Long getVersion() { return version; }
}
