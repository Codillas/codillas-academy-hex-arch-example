ALTER TABLE orders
    DROP CONSTRAINT orders_status_check;

ALTER TABLE orders
    ADD CONSTRAINT orders_status_check
        CHECK (status IN ('PLACED', 'CONFIRMED', 'CANCELLED'));

ALTER TABLE orders
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;

UPDATE orders
SET updated_at = created_at;

ALTER TABLE orders
    ALTER COLUMN updated_at SET NOT NULL;
