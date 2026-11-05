CREATE TABLE appealable_sanctions
(
    case_id           VARCHAR(36)  PRIMARY KEY,
    sanction_event_id VARCHAR(36)  NOT NULL UNIQUE,
    assignment_id     VARCHAR(36)  NOT NULL,
    player_id         VARCHAR(100) NOT NULL,
    sanction_type     VARCHAR(40)  NOT NULL,
    duration_seconds  BIGINT,
    status            VARCHAR(30)  NOT NULL,
    applied_at        TIMESTAMP(6) NOT NULL,
    version           BIGINT       NOT NULL DEFAULT 0,
    INDEX idx_appealable_player_status (player_id,status)
);
CREATE TABLE appeals
(
    id             VARCHAR(36)   PRIMARY KEY,
    case_id        VARCHAR(36)   NOT NULL UNIQUE,
    player_id      VARCHAR(100)  NOT NULL,
    reason         VARCHAR(2000) NOT NULL,
    status         VARCHAR(30)   NOT NULL,
    decision_notes VARCHAR(2000),
    decided_by     VARCHAR(100),
    submitted_at   TIMESTAMP(6)  NOT NULL,
    decided_at     TIMESTAMP(6),
    version        BIGINT        NOT NULL DEFAULT 0,
    INDEX idx_appeal_status_submitted (status,submitted_at)
);
CREATE TABLE processed_events
(
    event_id     VARCHAR(36)  PRIMARY KEY,
    event_type   VARCHAR(150) NOT NULL,
    aggregate_id VARCHAR(36)  NOT NULL,
    processed_at TIMESTAMP(6) NOT NULL,
    INDEX idx_appeal_processed_aggregate (aggregate_id)
);
CREATE TABLE appeal_outbox_events
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
    INDEX idx_appeal_outbox_status_created (status,next_attempt_at,created_at)
);
