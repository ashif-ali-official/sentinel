CREATE TABLE assignment_outbox_events
(
    id             VARCHAR(36) PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id   VARCHAR(36)  NOT NULL,
    event_type     VARCHAR(150) NOT NULL,
    topic          VARCHAR(200) NOT NULL,
    payload        JSON         NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    attempts       INT          NOT NULL DEFAULT 0,
    created_at     TIMESTAMP(6) NOT NULL,
    published_at   TIMESTAMP(6),
    last_error     VARCHAR(2000),

    INDEX          idx_assignment_outbox_status_created (
        status,
        created_at
    )
);