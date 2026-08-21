package com.codillas.academy.commerce.inventory.application.port.in;

import java.util.UUID;

public final class InventoryUnavailableException extends RuntimeException {

    private final UUID productId;

    public InventoryUnavailableException(UUID productId) {
        super("No inventory exists for product %s".formatted(productId));
        this.productId = productId;
    }

    public UUID productId() {
        return productId;
    }
}
