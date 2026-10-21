CREATE SCHEMA IF NOT EXISTS inventory;

CREATE TABLE IF NOT EXISTS inventory.stock (
    product_id INTEGER PRIMARY KEY CHECK (product_id > 0),
    quantity INTEGER NOT NULL CHECK (quantity >= 0)
);
