CREATE TABLE processed_events
(
    event_id     VARCHAR(36) PRIMARY KEY,
    event_type   VARCHAR(150) NOT NULL,
    aggregate_id VARCHAR(36)  NOT NULL,
    processed_at TIMESTAMP(6) NOT NULL,

    INDEX        idx_processed_events_aggregate (aggregate_id)
);