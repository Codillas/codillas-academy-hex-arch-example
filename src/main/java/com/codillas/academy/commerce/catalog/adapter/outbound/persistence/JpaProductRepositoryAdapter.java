package com.codillas.academy.commerce.catalog.adapter.outbound.persistence;

import com.codillas.academy.commerce.catalog.application.port.outbound.ProductRepository;
import com.codillas.academy.commerce.catalog.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository repository;

    JpaProductRepositoryAdapter(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        return repository.save(ProductJpaEntity.fromDomain(product)).toDomain();
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return repository.findById(productId).map(ProductJpaEntity::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(ProductJpaEntity::toDomain)
                .toList();
    }
}
