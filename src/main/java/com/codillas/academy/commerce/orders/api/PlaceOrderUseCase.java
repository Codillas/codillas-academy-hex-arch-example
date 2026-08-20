package com.codillas.academy.commerce.orders.api;

public interface PlaceOrderUseCase {

    OrderResult place(PlaceOrderCommand command);
}
