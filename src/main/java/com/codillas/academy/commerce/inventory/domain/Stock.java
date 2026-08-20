package com.codillas.academy.commerce.inventory.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Stock(UUID productId, int availableQuantity, Instant updatedAt) {

    public Stock {
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("availableQuantity must not be negative");
        }
    }

    public static Stock empty(UUID productId, Instant now) {
        return new Stock(productId, 0, now);
    }

    public Stock restock(int quantity, Instant now) {
        requirePositive(quantity);
        return changeTo(Math.addExact(availableQuantity, quantity), now);
    }

    public Stock reserve(int quantity, Instant now) {
        requirePositive(quantity);
        if (quantity > availableQuantity) {
            throw new InsufficientStockDomainException(productId, quantity, availableQuantity);
        }
        return changeTo(availableQuantity - quantity, now);
    }

    public Stock release(int quantity, Instant now) {
        requirePositive(quantity);
        return changeTo(Math.addExact(availableQuantity, quantity), now);
    }

    private Stock changeTo(int newQuantity, Instant now) {
        Objects.requireNonNull(now, "stock change time must not be null");
        if (now.isBefore(updatedAt)) {
            throw new IllegalArgumentException("stock change time must not be before updatedAt");
        }
        return new Stock(productId, newQuantity, now);
    }

    private static void requirePositive(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }
}
