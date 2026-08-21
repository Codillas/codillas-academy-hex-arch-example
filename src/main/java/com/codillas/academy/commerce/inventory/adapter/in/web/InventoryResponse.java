package com.codillas.academy.commerce.inventory.adapter.in.web;

import com.codillas.academy.commerce.inventory.application.port.in.InventoryResult;

import java.time.Instant;
import java.util.UUID;

record InventoryResponse(UUID productId, int availableQuantity, Instant updatedAt) {

    static InventoryResponse from(InventoryResult result) {
        return new InventoryResponse(result.productId(), result.availableQuantity(), result.updatedAt());
    }
}
