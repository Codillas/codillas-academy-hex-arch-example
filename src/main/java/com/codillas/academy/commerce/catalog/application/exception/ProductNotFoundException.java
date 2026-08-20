package com.codillas.academy.commerce.catalog.application.exception;

import java.util.UUID;

public final class ProductNotFoundException extends RuntimeException {

    private final UUID productId;

    public ProductNotFoundException(UUID productId) {
        super("Product %s was not found".formatted(productId));
        this.productId = productId;
    }

    public UUID productId() {
        return productId;
    }
}
