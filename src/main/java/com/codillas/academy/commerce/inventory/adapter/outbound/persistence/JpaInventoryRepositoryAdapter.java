package com.codillas.academy.commerce.inventory.adapter.outbound.persistence;

import com.codillas.academy.commerce.inventory.application.port.outbound.InventoryRepository;
import com.codillas.academy.commerce.inventory.domain.Stock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class JpaInventoryRepositoryAdapter implements InventoryRepository {

    private final SpringDataInventoryRepository repository;

    JpaInventoryRepositoryAdapter(SpringDataInventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Stock save(Stock stock) {
        return repository.save(InventoryJpaEntity.fromDomain(stock)).toDomain();
    }

    @Override
    public Optional<Stock> findByProductId(UUID productId) {
        return repository.findById(productId).map(InventoryJpaEntity::toDomain);
    }

    @Override
    public Optional<Stock> findByProductIdForUpdate(UUID productId) {
        return repository.findByProductIdForUpdate(productId).map(InventoryJpaEntity::toDomain);
    }
}
