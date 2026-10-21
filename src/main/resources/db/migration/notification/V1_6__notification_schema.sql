CREATE SCHEMA IF NOT EXISTS notification;

CREATE TABLE IF NOT EXISTS notification.delivery (
    order_id INTEGER NOT NULL CHECK (order_id > 0),
    kind VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id, kind),
    CONSTRAINT delivery_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);
