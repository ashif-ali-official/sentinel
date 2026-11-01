package com.sentinel.report.entity;

import com.sentinel.report.enums.ReportCategory;
import com.sentinel.report.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "reporter_id", nullable = false, length = 100)
    private String reporterId;

    @Column(name = "reported_player_id", nullable = false, length = 100)
    private String reportedPlayerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ReportCategory category;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReportStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Report() {
        // Required by JPA
    }

    public Report(
            String reporterId,
            String reportedPlayerId,
            ReportCategory category,
            String description
    ) {
        this.id = UUID.randomUUID().toString();
        this.reporterId = reporterId;
        this.reportedPlayerId = reportedPlayerId;
        this.category = category;
        this.description = description;
        this.status = ReportStatus.SUBMITTED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getReporterId() {
        return reporterId;
    }

    public String getReportedPlayerId() {
        return reportedPlayerId;
    }

    public ReportCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
