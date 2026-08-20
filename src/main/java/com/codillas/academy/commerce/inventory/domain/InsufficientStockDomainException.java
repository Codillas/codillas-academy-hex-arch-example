package com.codillas.academy.commerce.inventory.domain;

import java.util.UUID;

public final class InsufficientStockDomainException extends RuntimeException {

    private final UUID productId;
    private final int requestedQuantity;
    private final int availableQuantity;

    InsufficientStockDomainException(UUID productId, int requestedQuantity, int availableQuantity) {
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
