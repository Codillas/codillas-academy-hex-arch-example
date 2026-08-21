package com.codillas.academy.commerce.orders.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResult(
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
}
