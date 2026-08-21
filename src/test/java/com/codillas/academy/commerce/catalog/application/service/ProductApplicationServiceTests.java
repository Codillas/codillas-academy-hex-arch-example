package com.codillas.academy.commerce.catalog.application.service;

import com.codillas.academy.commerce.catalog.application.port.in.CreateProductCommand;
import com.codillas.academy.commerce.catalog.application.port.in.ProductNotFoundException;
import com.codillas.academy.commerce.catalog.application.port.out.ProductRepository;
import com.codillas.academy.commerce.catalog.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductApplicationServiceTests {

    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void createsListsAndDeactivatesProductsThroughPorts() {
        var service = new ProductApplicationService(new InMemoryProductRepository(), CLOCK);

        var created = service.create(new CreateProductCommand("Keyboard", new BigDecimal("129.90")));
        var deactivated = service.deactivate(created.id());

        assertThat(service.get(created.id())).isEqualTo(deactivated);
        assertThat(service.list()).containsExactly(deactivated);
        assertThat(service.findAvailableById(created.id())).isEmpty();
    }

    @Test
    void exposesActiveProductsToOtherModules() {
        var service = new ProductApplicationService(new InMemoryProductRepository(), CLOCK);
        var created = service.create(new CreateProductCommand("Keyboard", new BigDecimal("129.90")));

        assertThat(service.findAvailableById(created.id())).contains(created);
    }

    @Test
    void reportsAnUnknownProduct() {
        var service = new ProductApplicationService(new InMemoryProductRepository(), CLOCK);

        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    private static final class InMemoryProductRepository implements ProductRepository {

        private final LinkedHashMap<UUID, Product> products = new LinkedHashMap<>();

        @Override
        public Product save(Product product) {
            products.put(product.id(), product);
            return product;
        }

        @Override
        public Optional<Product> findById(UUID productId) {
            return Optional.ofNullable(products.get(productId));
        }

        @Override
        public List<Product> findAll() {
            return new ArrayList<>(products.values());
        }
    }
}
