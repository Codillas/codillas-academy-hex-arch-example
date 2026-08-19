package com.example.hexagonalorders.order.internal.persistence;

import com.example.hexagonalorders.order.internal.domain.Order;
import com.example.hexagonalorders.order.internal.port.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository repository;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return repository.save(OrderJpaEntity.fromDomain(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }
}
