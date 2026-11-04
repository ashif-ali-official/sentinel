CREATE TABLE notifications
(
    id                VARCHAR(36) PRIMARY KEY,
    recipient_id      VARCHAR(100)  NOT NULL,
    notification_type VARCHAR(50)   NOT NULL,
    title             VARCHAR(200)  NOT NULL,
    message           VARCHAR(1000) NOT NULL,
    reference_id      VARCHAR(36)   NOT NULL,
    is_read           BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP(6)  NOT NULL,
    read_at           TIMESTAMP(6),

    INDEX             idx_notifications_recipient_created (recipient_id, created_at),
    INDEX             idx_notifications_recipient_read (recipient_id, is_read, created_at)
);