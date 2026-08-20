package com.codillas.academy.commerce.orders.api;

import java.util.UUID;

public interface ConfirmOrderUseCase {

    OrderResult confirm(UUID orderId);
}
