package com.codillas.academy.commerce.catalog.adapter.inbound.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

record CreateProductRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal price
) {
}
