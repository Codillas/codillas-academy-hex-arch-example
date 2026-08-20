package com.codillas.academy.commerce.inventory.application;

import com.codillas.academy.commerce.catalog.api.ProductCatalog;
import com.codillas.academy.commerce.inventory.api.InventoryOperations;
import com.codillas.academy.commerce.inventory.api.InventoryResult;
import com.codillas.academy.commerce.inventory.api.InventoryUseCases;
import com.codillas.academy.commerce.inventory.api.InventoryUnavailableException;
import com.codillas.academy.commerce.inventory.api.InsufficientStockException;
import com.codillas.academy.commerce.inventory.domain.InsufficientStockDomainException;
import com.codillas.academy.commerce.inventory.domain.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InventoryApplicationService implements
        InventoryUseCases,
        InventoryOperations {

    private final InventoryRepository inventoryRepository;
    private final ProductCatalog productCatalog;
    private final Clock clock;

    public InventoryApplicationService(
            InventoryRepository inventoryRepository,
            ProductCatalog productCatalog,
            Clock clock
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productCatalog = productCatalog;
        this.clock = clock;
    }

    @Override
    @Transactional
    public InventoryResult restock(UUID productId, int quantity) {
        productCatalog.findAvailableById(productId)
                .orElseThrow(() -> new ProductUnavailableForInventoryException(productId));
        var now = now();
        var stock = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseGet(() -> Stock.empty(productId, now));
        return toResult(inventoryRepository.save(stock.restock(quantity, now)));
    }

    @Override
    public InventoryResult get(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .map(InventoryApplicationService::toResult)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }

    @Override
    @Transactional
    public InventoryResult reserve(UUID productId, int quantity) {
        var stock = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new InsufficientStockException(productId, quantity, 0));
        try {
            return toResult(inventoryRepository.save(stock.reserve(quantity, now())));
        } catch (InsufficientStockDomainException exception) {
            throw new InsufficientStockException(
                    exception.productId(),
                    exception.requestedQuantity(),
                    exception.availableQuantity()
            );
        }
    }

    @Override
    @Transactional
    public InventoryResult release(UUID productId, int quantity) {
        var stock = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new InventoryUnavailableException(productId));
        return toResult(inventoryRepository.save(stock.release(quantity, now())));
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private static InventoryResult toResult(Stock stock) {
        return new InventoryResult(stock.productId(), stock.availableQuantity(), stock.updatedAt());
    }
}
