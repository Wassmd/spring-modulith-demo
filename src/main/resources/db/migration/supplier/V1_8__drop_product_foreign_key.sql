-- Supplier references products by ID only. A foreign key into the product schema
-- would couple both modules at database level, so data ownership stays per module.
ALTER TABLE supplier.supplier DROP CONSTRAINT IF EXISTS supplier_product_id_fkey;
