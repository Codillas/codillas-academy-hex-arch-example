package com.example.hexagonalorders.order.internal.persistence;

import com.example.hexagonalorders.order.internal.domain.Order;
import com.example.hexagonalorders.order.internal.domain.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String product;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected OrderJpaEntity() {
    }

    private OrderJpaEntity(UUID id, String product, int quantity, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    static OrderJpaEntity fromDomain(Order order) {
        return new OrderJpaEntity(
                order.id(),
                order.product(),
                order.quantity(),
                order.status(),
                order.createdAt()
        );
    }

    Order toDomain() {
        return new Order(id, product, quantity, status, createdAt);
    }
}
