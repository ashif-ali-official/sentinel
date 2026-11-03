CREATE TABLE case_audit_logs
(
    id           VARCHAR(36) PRIMARY KEY,
    case_id      VARCHAR(36)  NOT NULL,
    action       VARCHAR(100) NOT NULL,
    old_value    VARCHAR(500),
    new_value    VARCHAR(500),
    performed_by VARCHAR(100) NOT NULL,
    created_at   TIMESTAMP(6) NOT NULL,

    INDEX        idx_audit_case_created (case_id, created_at)
);