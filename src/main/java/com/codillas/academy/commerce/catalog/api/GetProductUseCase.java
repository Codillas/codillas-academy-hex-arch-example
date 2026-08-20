package com.codillas.academy.commerce.catalog.api;

import java.util.UUID;

public interface GetProductUseCase {

    ProductResult get(UUID productId);
}
