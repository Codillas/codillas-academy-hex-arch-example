package com.codillas.academy.commerce.inventory.application.port.in;

import java.time.Instant;
import java.util.UUID;

public record InventoryResult(
        UUID productId,
        int availableQuantity,
        Instant updatedAt
) {
}
