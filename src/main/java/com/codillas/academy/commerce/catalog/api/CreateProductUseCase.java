package com.codillas.academy.commerce.catalog.api;

public interface CreateProductUseCase {

    ProductResult create(CreateProductCommand command);
}
