package com.codillas.academy.commerce.catalog.application;

import com.codillas.academy.commerce.catalog.domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    List<Product> findAll();
}
