package com.codillas.academy.commerce.inventory.application.exception;

import java.util.UUID;

public final class InventoryNotFoundException extends RuntimeException {

    private final UUID productId;

    public InventoryNotFoundException(UUID productId) {
        super("No inventory exists for product %s".formatted(productId));
        this.productId = productId;
    }

    public UUID productId() {
        return productId;
    }
}
