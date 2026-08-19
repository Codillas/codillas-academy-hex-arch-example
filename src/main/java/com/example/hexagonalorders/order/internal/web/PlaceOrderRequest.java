package com.example.hexagonalorders.order.internal.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PlaceOrderRequest(
        @NotBlank @Size(max = 200) String product,
        @Positive int quantity
) {
}
