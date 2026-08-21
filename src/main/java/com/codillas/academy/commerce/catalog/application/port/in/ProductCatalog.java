package com.codillas.academy.commerce.catalog.application.port.in;

import java.util.Optional;
import java.util.UUID;

/** Public product lookup contract for other business modules. */
public interface ProductCatalog {

    Optional<ProductResult> findAvailableById(UUID productId);
}
