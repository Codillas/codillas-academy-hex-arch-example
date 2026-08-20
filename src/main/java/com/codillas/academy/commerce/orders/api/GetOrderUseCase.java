package com.codillas.academy.commerce.orders.api;

import java.util.UUID;

public interface GetOrderUseCase {

    OrderResult get(UUID orderId);
}
