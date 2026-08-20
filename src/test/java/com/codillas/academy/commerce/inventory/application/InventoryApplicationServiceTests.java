package com.codillas.academy.commerce.inventory.application;

import com.codillas.academy.commerce.catalog.api.ProductCatalog;
import com.codillas.academy.commerce.catalog.api.ProductResult;
import com.codillas.academy.commerce.inventory.api.InsufficientStockException;
import com.codillas.academy.commerce.inventory.domain.Stock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryApplicationServiceTests {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final ProductResult PRODUCT = new ProductResult(
            PRODUCT_ID,
            "Keyboard",
            new BigDecimal("129.90"),
            true,
            NOW,
            NOW
    );

    @Test
    void restocksReservesAndReleasesThroughPorts() {
        var service = new InventoryApplicationService(
                new InMemoryInventoryRepository(),
                productId -> Optional.of(PRODUCT).filter(product -> product.id().equals(productId)),
                CLOCK
        );

        assertThat(service.restock(PRODUCT_ID, 5).availableQuantity()).isEqualTo(5);
        assertThat(service.reserve(PRODUCT_ID, 2).availableQuantity()).isEqualTo(3);
        assertThat(service.release(PRODUCT_ID, 1).availableQuantity()).isEqualTo(4);
        assertThat(service.get(PRODUCT_ID).availableQuantity()).isEqualTo(4);
    }

    @Test
    void translatesAStockRuleViolationToThePublicApiException() {
        var service = new InventoryApplicationService(
                new InMemoryInventoryRepository(),
                productId -> Optional.of(PRODUCT),
                CLOCK
        );
        service.restock(PRODUCT_ID, 2);

        assertThatThrownBy(() -> service.reserve(PRODUCT_ID, 3))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("2 units available");
    }

    @Test
    void refusesToStockAnUnavailableProduct() {
        ProductCatalog emptyCatalog = productId -> Optional.empty();
        var service = new InventoryApplicationService(
                new InMemoryInventoryRepository(),
                emptyCatalog,
                CLOCK
        );

        assertThatThrownBy(() -> service.restock(PRODUCT_ID, 1))
                .isInstanceOf(ProductUnavailableForInventoryException.class);
    }

    private static final class InMemoryInventoryRepository implements InventoryRepository {

        private Stock stock;

        @Override
        public Stock save(Stock stock) {
            this.stock = stock;
            return stock;
        }

        @Override
        public Optional<Stock> findByProductId(UUID productId) {
            return Optional.ofNullable(stock).filter(value -> value.productId().equals(productId));
        }

        @Override
        public Optional<Stock> findByProductIdForUpdate(UUID productId) {
            return findByProductId(productId);
        }
    }
}
