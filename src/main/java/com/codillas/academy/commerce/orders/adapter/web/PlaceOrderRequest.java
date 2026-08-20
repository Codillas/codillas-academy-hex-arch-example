package com.codillas.academy.commerce.orders.adapter.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

record PlaceOrderRequest(
        @NotNull UUID customerId,
        @NotNull UUID productId,
        @Positive int quantity
) {
}
