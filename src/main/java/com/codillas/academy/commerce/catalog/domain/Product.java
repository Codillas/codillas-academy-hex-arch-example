package com.codillas.academy.commerce.catalog.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Product(
        UUID id,
        String name,
        BigDecimal price,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public Product {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        name = name.strip();
        if (name.isEmpty() || name.length() > 200) {
            throw new IllegalArgumentException("name must contain between 1 and 200 characters");
        }
        try {
            price = price.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("price must have at most two decimal places", exception);
        }
        if (price.signum() <= 0) {
            throw new IllegalArgumentException("price must be positive");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt");
        }
    }

    public static Product create(String name, BigDecimal price, Instant now) {
        return new Product(UUID.randomUUID(), name, price, true, now, now);
    }

    public Product deactivate(Instant now) {
        if (!active) {
            return this;
        }
        Objects.requireNonNull(now, "deactivation time must not be null");
        if (now.isBefore(updatedAt)) {
            throw new IllegalArgumentException("deactivation time must not be before updatedAt");
        }
        return new Product(id, name, price, false, createdAt, now);
    }
}
