package com.codillas.academy.commerce.inventory.application.port.in;

import java.util.UUID;

public final class InsufficientStockException extends RuntimeException {

    private final UUID productId;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(UUID productId, int requestedQuantity, int availableQuantity) {
        super("Product %s has %d units available; %d were requested"
                .formatted(productId, availableQuantity, requestedQuantity));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public UUID productId() {
        return productId;
    }

    public int requestedQuantity() {
        return requestedQuantity;
    }

    public int availableQuantity() {
        return availableQuantity;
    }
}
