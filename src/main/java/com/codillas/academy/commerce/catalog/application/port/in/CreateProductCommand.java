package com.codillas.academy.commerce.catalog.application.port.in;

import java.math.BigDecimal;
import java.util.Objects;

public record CreateProductCommand(String name, BigDecimal price) {

    public CreateProductCommand {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(price, "price must not be null");
    }
}
