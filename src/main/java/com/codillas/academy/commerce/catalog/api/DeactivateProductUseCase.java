package com.codillas.academy.commerce.catalog.api;

import java.util.UUID;

public interface DeactivateProductUseCase {

    ProductResult deactivate(UUID productId);
}
