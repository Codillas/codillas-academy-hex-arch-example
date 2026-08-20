package com.codillas.academy.commerce.inventory.api;

import java.util.UUID;

public interface GetInventoryUseCase {

    InventoryResult get(UUID productId);
}
