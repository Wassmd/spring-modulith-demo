-- "order" is a reserved SQL word, so the module uses the schema "orders".
CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE IF NOT EXISTS orders.orders (
    id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS orders.line_item (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL CHECK (product_id > 0),
    quantity INTEGER NOT NULL CHECK (quantity > 0)
);
