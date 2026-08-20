package com.codillas.academy.commerce.inventory.application.exception;

import java.util.UUID;

public final class ProductUnavailableForInventoryException extends RuntimeException {

    public ProductUnavailableForInventoryException(UUID productId) {
        super("Product %s does not exist or is inactive".formatted(productId));
    }
}
