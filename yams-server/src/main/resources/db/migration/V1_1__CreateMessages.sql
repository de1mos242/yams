CREATE TABLE messages
(
    id                BIGSERIAL NOT NULL PRIMARY KEY,
    sender_id         BIGINT NOT NULL,
    receiver_id       BIGINT NOT NULL,
    content           TEXT   NOT NULL,
    message_timestamp TIMESTAMPTZ,

    FOREIGN KEY (sender_id) REFERENCES users (id),
    FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE INDEX messages_sender_idx on messages(sender_id);
CREATE INDEX messages_receiver_idx on messages(receiver_id);