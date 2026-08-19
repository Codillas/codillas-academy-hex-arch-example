CREATE TABLE orders (
    id UUID PRIMARY KEY,
    product VARCHAR(200) NOT NULL CHECK (length(trim(product)) > 0),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    status VARCHAR(32) NOT NULL CHECK (status IN ('PLACED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX orders_created_at_idx ON orders (created_at DESC);
