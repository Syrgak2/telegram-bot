-- liquibase formatted sql

-- changeset karybekov:1
CREATE TABLE IF NOT EXISTS notification_task (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL UNIQUE,
    send_date TIMESTAMP NOT NULL
)