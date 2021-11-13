CREATE TABLE messages
(
    id                BIGINT NOT NULL PRIMARY KEY,
    sender_id         BIGINT NOT NULL,
    receiver_id       BIGINT NOT NULL,
    content           TEXT   NOT NULL,
    message_timestamp TIMESTAMP,

    FOREIGN KEY (sender_id) REFERENCES users (id),
    FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE SEQUENCE message_id_seq
    START 1;

CREATE INDEX messages_sender_idx on messages(sender_id);
CREATE INDEX messages_receiver_idx on messages(receiver_id);