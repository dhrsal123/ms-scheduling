--liquibase formatted sql

--changeset cinema-system:1
-- Description: Create scheduled_movie table for managing movie showtimes
CREATE TABLE IF NOT EXISTS scheduled_movie
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    theater_id UUID        NOT NULL,
    room_id    UUID        NOT NULL,
    movie_id   UUID        NOT NULL,
    start      TIMESTAMP   NOT NULL,
    "end"        TIMESTAMP   NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_scheduled_movie_theater_room ON scheduled_movie (theater_id, room_id);
CREATE INDEX IF NOT EXISTS idx_scheduled_movie_movie_id ON scheduled_movie (movie_id);
CREATE INDEX IF NOT EXISTS idx_scheduled_movie_start_time ON scheduled_movie (start);

--changeset cinema-system:2
-- Description: Add CHECK constraint to ensure end time is after start time
ALTER TABLE scheduled_movie
    ADD CONSTRAINT chk_scheduled_movie_time_order CHECK (end > start);