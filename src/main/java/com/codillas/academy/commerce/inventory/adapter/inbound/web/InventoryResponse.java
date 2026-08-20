package com.codillas.academy.commerce.inventory.adapter.inbound.web;

import com.codillas.academy.commerce.inventory.api.InventoryResult;

import java.time.Instant;
import java.util.UUID;

record InventoryResponse(UUID productId, int availableQuantity, Instant updatedAt) {

    static InventoryResponse from(InventoryResult result) {
        return new InventoryResponse(result.productId(), result.availableQuantity(), result.updatedAt());
    }
}
