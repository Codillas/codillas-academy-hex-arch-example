package com.codillas.academy.commerce.catalog.adapter.outbound.persistence;

import com.codillas.academy.commerce.catalog.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
class ProductJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductJpaEntity() {
    }

    private ProductJpaEntity(
            UUID id,
            String name,
            BigDecimal price,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    static ProductJpaEntity fromDomain(Product product) {
        return new ProductJpaEntity(
                product.id(),
                product.name(),
                product.price(),
                product.active(),
                product.createdAt(),
                product.updatedAt()
        );
    }

    Product toDomain() {
        return new Product(id, name, price, active, createdAt, updatedAt);
    }
}
