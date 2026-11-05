ALTER TABLE sanction_reviews
    ADD COLUMN reversed_at TIMESTAMP(6) NULL,
    ADD COLUMN reversal_reason VARCHAR(2000) NULL;
