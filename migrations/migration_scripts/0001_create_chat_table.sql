--liquibase formatted sql
CREATE TABLE IF NOT EXISTS db.chat (
    id  BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);
