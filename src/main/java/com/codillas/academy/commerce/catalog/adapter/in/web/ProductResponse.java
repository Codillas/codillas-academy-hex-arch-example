package com.codillas.academy.commerce.catalog.adapter.in.web;

import com.codillas.academy.commerce.catalog.application.port.in.ProductResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    static ProductResponse from(ProductResult result) {
        return new ProductResponse(
                result.id(),
                result.name(),
                result.price(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
