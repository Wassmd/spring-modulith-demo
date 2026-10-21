-- Runs after the product migration because of the foreign key to product.product.
CREATE SCHEMA IF NOT EXISTS supplier;

CREATE TABLE IF NOT EXISTS supplier.supplier (
    id BIGSERIAL PRIMARY KEY,
    supplier_name VARCHAR(255),
    product_id BIGINT REFERENCES product.product(id),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP,
    modified_by VARCHAR(255)
);
