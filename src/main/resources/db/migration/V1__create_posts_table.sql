CREATE TABLE posts (
    id        BIGINT       PRIMARY KEY,
    user_id   BIGINT       NOT NULL,
    title     VARCHAR(500) NOT NULL,
    body      TEXT         NOT NULL,
    synced_at TIMESTAMPTZ  NOT NULL,
    version   BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_posts_user_id ON posts (user_id);
