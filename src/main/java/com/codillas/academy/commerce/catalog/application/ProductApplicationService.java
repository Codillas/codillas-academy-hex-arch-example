package com.codillas.academy.commerce.catalog.application;

import com.codillas.academy.commerce.catalog.api.CreateProductCommand;
import com.codillas.academy.commerce.catalog.api.ProductCatalog;
import com.codillas.academy.commerce.catalog.api.ProductResult;
import com.codillas.academy.commerce.catalog.api.ProductUseCases;
import com.codillas.academy.commerce.catalog.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductApplicationService implements
        ProductUseCases,
        ProductCatalog {

    private final ProductRepository productRepository;
    private final Clock clock;

    public ProductApplicationService(ProductRepository productRepository, Clock clock) {
        this.productRepository = productRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public ProductResult create(CreateProductCommand command) {
        var product = Product.create(command.name(), command.price(), now());
        return toResult(productRepository.save(product));
    }

    @Override
    public ProductResult get(UUID productId) {
        return productRepository.findById(productId)
                .map(ProductApplicationService::toResult)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public List<ProductResult> list() {
        return productRepository.findAll().stream()
                .map(ProductApplicationService::toResult)
                .toList();
    }

    @Override
    @Transactional
    public ProductResult deactivate(UUID productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return toResult(productRepository.save(product.deactivate(now())));
    }

    @Override
    public Optional<ProductResult> findAvailableById(UUID productId) {
        return productRepository.findById(productId)
                .filter(Product::active)
                .map(ProductApplicationService::toResult);
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private static ProductResult toResult(Product product) {
        return new ProductResult(
                product.id(),
                product.name(),
                product.price(),
                product.active(),
                product.createdAt(),
                product.updatedAt()
        );
    }
}
