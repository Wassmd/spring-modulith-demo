CREATE SCHEMA IF NOT EXISTS customer;

CREATE TABLE IF NOT EXISTS customer.customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    modified_at TIMESTAMP,
    modified_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS customer.address (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(255),
    city VARCHAR(255),
    zip_code VARCHAR(20),
    type SMALLINT,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_address_to_customer
        FOREIGN KEY (customer_id) REFERENCES customer.customer(id) ON DELETE CASCADE
);
