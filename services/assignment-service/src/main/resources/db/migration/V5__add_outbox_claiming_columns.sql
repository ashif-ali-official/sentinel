ALTER TABLE assignment_outbox_events
    ADD COLUMN claimed_by VARCHAR(100) NULL,
    ADD COLUMN claimed_at TIMESTAMP(6) NULL,
    ADD COLUMN next_attempt_at TIMESTAMP(6) NULL;

CREATE INDEX idx_assignment_outbox_claiming
    ON assignment_outbox_events (
                                 status,
                                 next_attempt_at,
                                 created_at
        );