package com.codillas.academy.commerce.orders.api;

import java.util.UUID;

public interface CancelOrderUseCase {

    OrderResult cancel(UUID orderId);
}
