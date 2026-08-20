package com.codillas.academy.commerce.orders.adapter.inbound.web;

import com.codillas.academy.commerce.orders.api.OrderResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

record OrderResponse(
        UUID id,
        UUID customerId,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal totalPrice,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    static OrderResponse from(OrderResult result) {
        return new OrderResponse(
                result.id(),
                result.customerId(),
                result.productId(),
                result.productName(),
                result.unitPrice(),
                result.quantity(),
                result.totalPrice(),
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
