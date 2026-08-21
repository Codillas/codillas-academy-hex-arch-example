package com.codillas.academy.commerce.orders.application.port.in;

import java.util.Objects;
import java.util.UUID;

public record PlaceOrderCommand(UUID customerId, UUID productId, int quantity) {

    public PlaceOrderCommand {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }
}
