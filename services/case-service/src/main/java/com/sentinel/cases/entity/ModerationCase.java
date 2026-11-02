package com.sentinel.cases.entity;

import com.sentinel.cases.enums.CasePriority;
import com.sentinel.cases.enums.CaseStatus;
import com.sentinel.cases.enums.ReportCategory;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "cases",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cases_report_id",
                        columnNames = "report_id"
                )
        }
)
public class ModerationCase {

    @Id
    private String id;

    @Column(name = "report_id", nullable = false, unique = true, length = 36)
    private String reportId;

    @Column(name = "reporter_id", nullable = false, length = 100)
    private String reporterId;

    @Column(name = "reported_player_id", nullable = false, length = 100)
    private String reportedPlayerId;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CaseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CasePriority priority;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ModerationCase() {
        // Required by JPA
    }

    public ModerationCase(
            String reportId,
            String reporterId,
            String reportedPlayerId,
            String description,
            ReportCategory category) {

        this.id = UUID.randomUUID().toString();
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reportedPlayerId = reportedPlayerId;
        this.description = description;
        this.category = category;
        this.status = CaseStatus.OPEN;
        this.priority = CasePriority.MEDIUM;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getReportId() {
        return reportId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public String getReportedPlayerId() {
        return reportedPlayerId;
    }

    public String getDescription() { return description; }

    public ReportCategory getCategory() {
        return category;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public CasePriority getPriority() {
        return priority;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateStatus(CaseStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Case status cannot be null");
        }

        if (!isValidTransition(newStatus)) {
            throw new IllegalStateException(
                    "Invalid case status transition: "
                            + this.status + " -> " + newStatus
            );
        }

        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    private boolean isValidTransition(CaseStatus newStatus) {

        return switch (this.status) {
            case OPEN ->
                    newStatus == CaseStatus.ASSIGNED
                            || newStatus == CaseStatus.UNDER_INVESTIGATION
                            || newStatus == CaseStatus.RESOLVED;

            case ASSIGNED ->
                    newStatus == CaseStatus.UNDER_INVESTIGATION
                            || newStatus == CaseStatus.RESOLVED;

            case UNDER_INVESTIGATION ->
                    newStatus == CaseStatus.RESOLVED;

            case RESOLVED ->
                    newStatus == CaseStatus.CLOSED;

            case CLOSED -> false;
        };
    }

    public void updatePriority(CasePriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Case priority cannot be null");
        }

        this.priority = priority;
        this.updatedAt = Instant.now();
    }

}
