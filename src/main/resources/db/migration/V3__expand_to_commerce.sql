CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL CHECK (length(trim(name)) > 0),
    email VARCHAR(320) NOT NULL UNIQUE CHECK (length(trim(email)) > 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX customers_created_at_idx ON customers (created_at DESC);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL CHECK (length(trim(name)) > 0),
    price NUMERIC(19, 2) NOT NULL CHECK (price > 0),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL CHECK (updated_at >= created_at)
);

CREATE INDEX products_created_at_idx ON products (created_at DESC);

CREATE TABLE inventory (
    product_id UUID PRIMARY KEY REFERENCES products (id),
    available_quantity INTEGER NOT NULL CHECK (available_quantity >= 0),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE orders
    RENAME COLUMN product TO product_name;

ALTER TABLE orders
    ADD COLUMN customer_id UUID,
    ADD COLUMN product_id UUID,
    ADD COLUMN unit_price NUMERIC(19, 2);

-- Preserve rows created by the earlier order-only version of this example.
INSERT INTO customers (id, name, email, created_at)
SELECT
    '00000000-0000-0000-0000-000000000001',
    'Legacy customer',
    'legacy@example.test',
    MIN(created_at)
FROM orders
HAVING COUNT(*) > 0;

INSERT INTO products (id, name, price, active, created_at, updated_at)
SELECT
    '00000000-0000-0000-0000-000000000002',
    'Legacy product',
    0.01,
    FALSE,
    MIN(created_at),
    MAX(updated_at)
FROM orders
HAVING COUNT(*) > 0;

UPDATE orders
SET customer_id = '00000000-0000-0000-0000-000000000001',
    product_id = '00000000-0000-0000-0000-000000000002',
    unit_price = 0.01;

ALTER TABLE orders
    ALTER COLUMN customer_id SET NOT NULL,
    ALTER COLUMN product_id SET NOT NULL,
    ALTER COLUMN unit_price SET NOT NULL,
    ADD CONSTRAINT orders_customer_fk FOREIGN KEY (customer_id) REFERENCES customers (id),
    ADD CONSTRAINT orders_product_fk FOREIGN KEY (product_id) REFERENCES products (id),
    ADD CONSTRAINT orders_unit_price_check CHECK (unit_price > 0);

CREATE INDEX orders_customer_id_idx ON orders (customer_id);
CREATE INDEX orders_product_id_idx ON orders (product_id);
