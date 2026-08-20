package com.codillas.academy.commerce.inventory.api;

import java.util.UUID;

/** Synchronous stock operations used by order workflows. */
public interface InventoryOperations {

    InventoryResult reserve(UUID productId, int quantity);

    InventoryResult release(UUID productId, int quantity);
}
