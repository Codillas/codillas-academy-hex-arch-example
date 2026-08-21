package com.codillas.academy.commerce.orders.adapter.out.persistence;

import com.codillas.academy.commerce.orders.application.port.out.OrderRepository;
import com.codillas.academy.commerce.orders.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaOrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository repository;

    JpaOrderRepositoryAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return repository.save(OrderJpaEntity.fromDomain(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return repository.findById(orderId).map(OrderJpaEntity::toDomain);
    }

    @Override
    public Optional<Order> findByIdForUpdate(UUID orderId) {
        return repository.findByIdForUpdate(orderId).map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }
}
