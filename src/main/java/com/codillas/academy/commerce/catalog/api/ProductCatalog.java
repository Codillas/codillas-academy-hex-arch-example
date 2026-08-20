package com.codillas.academy.commerce.catalog.api;

import java.util.Optional;
import java.util.UUID;

/** Public product lookup contract for other business modules. */
public interface ProductCatalog {

    Optional<ProductResult> findAvailableById(UUID productId);
}
