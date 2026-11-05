package com.sentinel.moderation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import com.sentinel.moderation.entity.enums.WorkItemStatus;
import com.sentinel.moderation.entity.enums.ModerationDecision;

import java.time.Instant;

@Entity
@Table(name = "moderation_work_items")
public class ModerationWorkItem {
    @Id
    @Column(name = "case_id", length = 36)
    private String caseId;

    @Column(name = "assignment_id", nullable = false, unique = true, length = 36)
    private String assignmentId;

    @Column(name = "moderator_id", nullable = false, length = 100)
    private String moderatorId;

    @Column(name = "reported_player_id", length = 100)
    private String reportedPlayerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkItemStatus status;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", length = 40)
    private ModerationDecision decision;

    @Column(name = "decision_notes", length = 2000)
    private String decisionNotes;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    protected ModerationWorkItem() {}

    public ModerationWorkItem(String caseId, String assignmentId, String moderatorId, String reportedPlayerId) {
        this.caseId = caseId;
        this.assignmentId = assignmentId;
        this.moderatorId = moderatorId;
        this.reportedPlayerId = reportedPlayerId;
        this.status = WorkItemStatus.ASSIGNED;
        this.assignedAt = Instant.now();
    }

    public void start(String requestingModeratorId) {
        if (!moderatorId.equals(requestingModeratorId)) {
            throw new IllegalArgumentException("Work item is assigned to a different moderator");
        }
        if (status == WorkItemStatus.IN_PROGRESS) return;
        if (status != WorkItemStatus.ASSIGNED) {
            throw new IllegalStateException("Cannot start work item in status " + status);
        }
        status = WorkItemStatus.IN_PROGRESS;
        startedAt = Instant.now();
    }

    public void decide(String requestingModeratorId, ModerationDecision decision, String notes) {
        if (!moderatorId.equals(requestingModeratorId)) {
            throw new IllegalArgumentException("Work item is assigned to a different moderator");
        }
        if (status != WorkItemStatus.IN_PROGRESS) {
            throw new IllegalStateException("Investigation must be in progress before a decision can be recorded");
        }
        if (decision == null) throw new IllegalArgumentException("Decision is required");
        if (reportedPlayerId == null || reportedPlayerId.isBlank()) {
            throw new IllegalStateException("Reported player ID is unavailable for this work item");
        }
        this.decision = decision;
        this.decisionNotes = notes;
        this.decidedAt = Instant.now();
        this.status = WorkItemStatus.DECIDED;
    }

    public String getCaseId() { return caseId; }
    public String getAssignmentId() { return assignmentId; }
    public String getModeratorId() { return moderatorId; }
    public String getReportedPlayerId() { return reportedPlayerId; }
    public WorkItemStatus getStatus() { return status; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Long getVersion() { return version; }
    public ModerationDecision getDecision() { return decision; }
    public String getDecisionNotes() { return decisionNotes; }
    public Instant getDecidedAt() { return decidedAt; }
}
