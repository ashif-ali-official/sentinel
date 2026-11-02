CREATE TABLE outbox_events (
   id VARCHAR(36) PRIMARY KEY,
   aggregate_type VARCHAR(100) NOT NULL,
   aggregate_id VARCHAR(36) NOT NULL,
   event_type VARCHAR(150) NOT NULL,
   payload JSON NOT NULL,
   status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
   created_at TIMESTAMP(6) NOT NULL,
   published_at TIMESTAMP(6) NULL,
   INDEX idx_outbox_status_created (status, created_at)
);