CREATE TABLE sanction_reviews
(
    case_id             VARCHAR(36)  PRIMARY KEY,
    assignment_id       VARCHAR(36)  NOT NULL,
    reported_player_id  VARCHAR(100) NOT NULL,
    moderator_id        VARCHAR(100) NOT NULL,
    moderation_decision VARCHAR(40)  NOT NULL,
    decision_notes      VARCHAR(2000),
    status              VARCHAR(30)  NOT NULL,
    sanction_type       VARCHAR(40),
    duration_seconds    BIGINT,
    actioned_by         VARCHAR(100),
    decided_at          TIMESTAMP(6) NOT NULL,
    actioned_at         TIMESTAMP(6),
    version             BIGINT       NOT NULL DEFAULT 0,
    INDEX idx_sanction_player_status (reported_player_id, status)
);

CREATE TABLE processed_events
(
    event_id     VARCHAR(36)  PRIMARY KEY,
    event_type   VARCHAR(150) NOT NULL,
    aggregate_id VARCHAR(36)  NOT NULL,
    processed_at TIMESTAMP(6) NOT NULL,
    INDEX idx_sanction_processed_aggregate (aggregate_id)
);

CREATE TABLE sanction_outbox_events
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
    INDEX idx_sanction_outbox_status_created (status, next_attempt_at, created_at),
    INDEX idx_sanction_outbox_aggregate (aggregate_id)
);
