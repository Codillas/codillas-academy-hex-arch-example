package com.codillas.academy.commerce.inventory.application.service;

import com.codillas.academy.commerce.catalog.application.port.in.ProductCatalog;
import com.codillas.academy.commerce.inventory.application.port.in.InventoryNotFoundException;
import com.codillas.academy.commerce.inventory.application.port.in.InventoryOperations;
import com.codillas.academy.commerce.inventory.application.port.in.InventoryResult;
import com.codillas.academy.commerce.inventory.application.port.in.InventoryUseCases;
import com.codillas.academy.commerce.inventory.application.port.in.InventoryUnavailableException;
import com.codillas.academy.commerce.inventory.application.port.in.InsufficientStockException;
import com.codillas.academy.commerce.inventory.application.port.in.ProductUnavailableForInventoryException;
import com.codillas.academy.commerce.inventory.application.port.out.InventoryRepository;
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
