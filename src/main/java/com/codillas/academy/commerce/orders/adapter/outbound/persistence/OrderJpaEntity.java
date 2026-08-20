package com.codillas.academy.commerce.orders.adapter.outbound.persistence;

import com.codillas.academy.commerce.orders.domain.Order;
import com.codillas.academy.commerce.orders.domain.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrderJpaEntity() {
    }

    private OrderJpaEntity(
            UUID id,
            UUID customerId,
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            OrderStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    static OrderJpaEntity fromDomain(Order order) {
        return new OrderJpaEntity(
                order.id(),
                order.customerId(),
                order.productId(),
                order.productName(),
                order.unitPrice(),
                order.quantity(),
                order.status(),
                order.createdAt(),
                order.updatedAt()
        );
    }

    Order toDomain() {
        return new Order(
                id,
                customerId,
                productId,
                productName,
                unitPrice,
                quantity,
                status,
                createdAt,
                updatedAt
        );
    }
}
