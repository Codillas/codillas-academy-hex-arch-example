package com.codillas.academy.commerce.inventory.application.port.in;

import java.util.UUID;

public interface InventoryUseCases {

    InventoryResult restock(UUID productId, int quantity);

    InventoryResult get(UUID productId);
}
