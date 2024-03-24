-- liquibase formatted sql

-- changeset karybekov:1
CREATE TABLE IF NOT EXISTS notification_task (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL UNIQUE,
    send_date TIMESTAMP NOT NULL
)

-- changeset karybekov:2
ALTER TABLE notification_task
ADD COLUMN message VARCHAR(255)

-- changeset karybekov:3
ALTER TABLE notification_task DROP CONSTRAINT notification_task_chat_id_key