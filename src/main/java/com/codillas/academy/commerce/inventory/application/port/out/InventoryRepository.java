package com.codillas.academy.commerce.inventory.application.port.out;

import com.codillas.academy.commerce.inventory.domain.Stock;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {

    Stock save(Stock stock);

    Optional<Stock> findByProductId(UUID productId);

    Optional<Stock> findByProductIdForUpdate(UUID productId);
}
