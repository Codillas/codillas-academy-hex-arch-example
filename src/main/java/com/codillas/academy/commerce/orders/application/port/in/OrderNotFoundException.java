package com.codillas.academy.commerce.orders.application.port.in;

import java.util.UUID;

public final class OrderNotFoundException extends RuntimeException {

    private final UUID orderId;

    public OrderNotFoundException(UUID orderId) {
        super("Order %s was not found".formatted(orderId));
        this.orderId = orderId;
    }

    public UUID orderId() {
        return orderId;
    }
}
