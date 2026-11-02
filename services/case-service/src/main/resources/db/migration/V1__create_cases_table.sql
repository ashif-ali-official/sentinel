CREATE TABLE cases
(
    id                 VARCHAR(36) PRIMARY KEY,
    report_id          VARCHAR(36)   NOT NULL,
    reporter_id        VARCHAR(100)  NOT NULL,
    reported_player_id VARCHAR(100)  NOT NULL,
    description        VARCHAR(2000) NOT NULL,
    category           VARCHAR(50)   NOT NULL,
    status             VARCHAR(30)   NOT NULL,
    priority           VARCHAR(30)   NOT NULL,
    created_at         TIMESTAMP(6)  NOT NULL,
    updated_at         TIMESTAMP(6)  NOT NULL,
    CONSTRAINT uk_cases_report_id UNIQUE (report_id),
    INDEX              idx_cases_reported_player (reported_player_id),
    INDEX              idx_cases_status_created (status, created_at),
    INDEX              idx_cases_priority_created (priority, created_at)
);