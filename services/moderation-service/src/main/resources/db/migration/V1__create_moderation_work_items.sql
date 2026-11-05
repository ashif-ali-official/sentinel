CREATE TABLE moderation_work_items
(
    case_id       VARCHAR(36)  PRIMARY KEY,
    assignment_id VARCHAR(36)  NOT NULL,
    moderator_id  VARCHAR(100) NOT NULL,
    status        VARCHAR(30)  NOT NULL,
    assigned_at   TIMESTAMP(6) NOT NULL,
    started_at    TIMESTAMP(6),
    version       BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uk_moderation_assignment UNIQUE (assignment_id),
    INDEX idx_moderation_moderator_status (moderator_id, status, assigned_at)
);

CREATE TABLE processed_events
(
    event_id     VARCHAR(36)  PRIMARY KEY,
    event_type   VARCHAR(150) NOT NULL,
    aggregate_id VARCHAR(36)  NOT NULL,
    processed_at TIMESTAMP(6) NOT NULL,
    INDEX idx_moderation_processed_aggregate (aggregate_id)
);
