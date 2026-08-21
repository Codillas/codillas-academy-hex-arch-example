package com.codillas.academy.commerce.inventory.adapter.out.persistence;

import com.codillas.academy.commerce.inventory.domain.Stock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory")
class InventoryJpaEntity {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InventoryJpaEntity() {
    }

    private InventoryJpaEntity(UUID productId, int availableQuantity, Instant updatedAt) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.updatedAt = updatedAt;
    }

    static InventoryJpaEntity fromDomain(Stock stock) {
        return new InventoryJpaEntity(stock.productId(), stock.availableQuantity(), stock.updatedAt());
    }

    Stock toDomain() {
        return new Stock(productId, availableQuantity, updatedAt);
    }
}
