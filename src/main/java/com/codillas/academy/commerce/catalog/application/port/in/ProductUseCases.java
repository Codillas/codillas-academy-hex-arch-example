package com.codillas.academy.commerce.catalog.application.port.in;

import java.util.List;
import java.util.UUID;

public interface ProductUseCases {

    ProductResult create(CreateProductCommand command);

    ProductResult get(UUID productId);

    List<ProductResult> list();

    ProductResult deactivate(UUID productId);
}
