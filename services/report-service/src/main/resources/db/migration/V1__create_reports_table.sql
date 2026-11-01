CREATE TABLE reports (
    id VARCHAR(36) PRIMARY KEY,
    reporter_id VARCHAR(100) NOT NULL,
    reported_player_id VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_reporter_id (reporter_id),
    INDEX idx_reported_player_id (reported_player_id),
    INDEX idx_status_created_at (status, created_at)
);