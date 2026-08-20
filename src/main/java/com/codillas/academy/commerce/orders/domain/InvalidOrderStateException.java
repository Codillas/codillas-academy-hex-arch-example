package com.codillas.academy.commerce.orders.domain;

import java.util.UUID;

public final class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(UUID orderId, OrderStatus currentStatus, OrderStatus targetStatus) {
        super("Order %s cannot transition from %s to %s"
                .formatted(orderId, currentStatus, targetStatus));
    }
}
