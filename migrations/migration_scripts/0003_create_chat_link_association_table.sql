--liquibase formatted sql
CREATE TABLE IF NOT EXISTS db.chat_link_association (
    chat_id BIGINT NOT NULL REFERENCES db.chat (id) ON DELETE CASCADE,
    link_id BIGINT NOT NULL REFERENCES db.link (id) ON DELETE CASCADE,

    PRIMARY KEY (chat_id, link_id)
);
