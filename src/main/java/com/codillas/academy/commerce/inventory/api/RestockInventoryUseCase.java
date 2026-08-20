package com.codillas.academy.commerce.inventory.api;

import java.util.UUID;

public interface RestockInventoryUseCase {

    InventoryResult restock(UUID productId, int quantity);
}
