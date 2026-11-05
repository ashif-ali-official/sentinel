ALTER TABLE cases
    ADD COLUMN assigned_moderator_id VARCHAR(100) NULL AFTER status;
