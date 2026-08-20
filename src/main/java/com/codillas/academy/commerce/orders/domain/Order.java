package com.codillas.academy.commerce.orders.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Order(
        UUID id,
        UUID customerId,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public Order {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(productName, "productName must not be null");
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");

        productName = productName.strip();
        if (productName.isEmpty() || productName.length() > 200) {
            throw new IllegalArgumentException("productName must contain between 1 and 200 characters");
        }
        try {
            unitPrice = unitPrice.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("unitPrice must have at most two decimal places", exception);
        }
        if (unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("unitPrice must be positive");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt must not be before createdAt");
        }
    }

    public static Order place(
            UUID customerId,
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            Instant now
    ) {
        return new Order(
                UUID.randomUUID(),
                customerId,
                productId,
                productName,
                unitPrice,
                quantity,
                OrderStatus.PLACED,
                now,
                now
        );
    }

    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Order confirm(Instant now) {
        if (status == OrderStatus.CONFIRMED) {
            return this;
        }
        if (status != OrderStatus.PLACED) {
            throw new InvalidOrderStateException(id, status, OrderStatus.CONFIRMED);
        }
        return transitionTo(OrderStatus.CONFIRMED, now);
    }

    public Order cancel(Instant now) {
        if (status == OrderStatus.CANCELLED) {
            return this;
        }
        return transitionTo(OrderStatus.CANCELLED, now);
    }

    private Order transitionTo(OrderStatus targetStatus, Instant now) {
        Objects.requireNonNull(now, "transition time must not be null");
        if (now.isBefore(updatedAt)) {
            throw new IllegalArgumentException("transition time must not be before updatedAt");
        }
        return new Order(
                id,
                customerId,
                productId,
                productName,
                unitPrice,
                quantity,
                targetStatus,
                createdAt,
                now
        );
    }
}
