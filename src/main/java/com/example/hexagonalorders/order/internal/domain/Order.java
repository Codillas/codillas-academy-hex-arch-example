package com.example.hexagonalorders.order.internal.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Order(
        UUID id,
        String product,
        int quantity,
        OrderStatus status,
        Instant createdAt
) {

    public Order {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(product, "product must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");

        product = product.strip();
        if (product.isEmpty()) {
            throw new IllegalArgumentException("product must not be blank");
        }
        if (product.length() > 200) {
            throw new IllegalArgumentException("product must be at most 200 characters");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }

    public static Order place(String product, int quantity) {
        return new Order(
                UUID.randomUUID(),
                product,
                quantity,
                OrderStatus.PLACED,
                Instant.now()
        );
    }
}
