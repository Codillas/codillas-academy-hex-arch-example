package com.codillas.academy.commerce.inventory.api;

import java.util.UUID;

public interface InventoryUseCases {

    InventoryResult restock(UUID productId, int quantity);

    InventoryResult get(UUID productId);
}
