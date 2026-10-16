CREATE SCHEMA IF NOT EXISTS product;
CREATE SCHEMA IF NOT EXISTS supplier;
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS orders;

-- Customer table
CREATE TABLE IF NOT EXISTS customer.customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    modified_at TIMESTAMP,
    modified_by VARCHAR(255)
);

-- Address table with foreign key to customer
CREATE TABLE IF NOT EXISTS customer.address (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(255),
    city VARCHAR(255),
    zip_code VARCHAR(20),
    type smallint,
    customer_id BIGINT NOT NULL,
    constraint fk_address_to_customer
    FOREIGN KEY (customer_id)
    references customer.customer(id)
    on delete cascade
);

-- Order aggregate (Spring Data JDBC)
CREATE SCHEMA IF NOT EXISTS "order";

CREATE TABLE IF NOT EXISTS "order"."order" (
    id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS orders_line_items (
    id SERIAL PRIMARY KEY,
    product INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    order_id INTEGER REFERENCES "order"."order"(id)
);
