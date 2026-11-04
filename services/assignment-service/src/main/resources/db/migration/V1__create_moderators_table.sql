CREATE TABLE moderators
(
    id               VARCHAR(36) PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL,
    display_name     VARCHAR(100) NOT NULL,
    region           VARCHAR(50)  NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    available        BOOLEAN      NOT NULL DEFAULT TRUE,
    max_active_cases INT          NOT NULL DEFAULT 10,
    created_at       TIMESTAMP(6) NOT NULL,
    updated_at       TIMESTAMP(6) NOT NULL,

    CONSTRAINT uk_moderators_username UNIQUE (username),

    INDEX            idx_moderators_eligibility (
        active,
        available,
        region
    )
);