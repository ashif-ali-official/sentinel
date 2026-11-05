ALTER TABLE moderation_work_items
    ADD COLUMN decision VARCHAR(40) NULL,
    ADD COLUMN decision_notes VARCHAR(2000) NULL,
    ADD COLUMN decided_at TIMESTAMP(6) NULL;

CREATE TABLE moderation_outbox_events
(
    id              VARCHAR(36)   PRIMARY KEY,
    aggregate_id    VARCHAR(36)   NOT NULL,
    event_type      VARCHAR(150)  NOT NULL,
    topic           VARCHAR(200)  NOT NULL,
    payload         JSON          NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    attempts        INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMP(6)  NOT NULL,
    next_attempt_at TIMESTAMP(6)  NOT NULL,
    claimed_at      TIMESTAMP(6),
    published_at    TIMESTAMP(6),
    last_error      VARCHAR(2000),
    INDEX idx_moderation_outbox_status_created (status, next_attempt_at, created_at),
    INDEX idx_moderation_outbox_aggregate (aggregate_id)
);
