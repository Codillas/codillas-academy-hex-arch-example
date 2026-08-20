package com.codillas.academy.commerce.catalog.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResult(
        UUID id,
        String name,
        BigDecimal price,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
