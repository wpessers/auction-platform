CREATE TABLE refresh_tokens (
    id            UUID         NOT NULL,
    user_id       UUID         NOT NULL,
    token_hash    VARCHAR(64)  NOT NULL,
    expires_at    TIMESTAMP    NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked       BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uc_refresh_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
