CREATE TABLE case_assignments
(
    id           VARCHAR(36) PRIMARY KEY,
    case_id      VARCHAR(36)  NOT NULL,
    moderator_id VARCHAR(36)  NOT NULL,
    status       VARCHAR(30)  NOT NULL,
    assigned_at  TIMESTAMP(6) NOT NULL,
    completed_at TIMESTAMP(6),

    CONSTRAINT uk_case_assignments_case_id UNIQUE (case_id),

    INDEX        idx_assignments_moderator_status (
        moderator_id,
        status
    ),

    CONSTRAINT fk_assignment_moderator
        FOREIGN KEY (moderator_id)
            REFERENCES moderators (id)
);