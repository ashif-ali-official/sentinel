CREATE TABLE players
(
    id           VARCHAR(36) PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    region       VARCHAR(50)  NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP(6) NOT NULL,
    updated_at   TIMESTAMP(6) NOT NULL,

    CONSTRAINT uk_players_username UNIQUE (username),
    CONSTRAINT uk_players_email UNIQUE (email),

    INDEX        idx_players_region (region),
    INDEX        idx_players_status_created (status, created_at)
);