package com.sentinel.cases.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "case_audit_logs")
public class CaseAuditLog {

    @Id
    private String id;

    @Column(name = "case_id", nullable = false, length = 36)
    private String caseId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "old_value", length = 500)
    private String oldValue;

    @Column(name = "new_value", length = 500)
    private String newValue;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CaseAuditLog() {
    }

    public CaseAuditLog(
            String caseId,
            String action,
            String oldValue,
            String newValue,
            String performedBy) {

        this.id = UUID.randomUUID().toString();
        this.caseId = caseId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.performedBy = performedBy;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getAction() {
        return action;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}