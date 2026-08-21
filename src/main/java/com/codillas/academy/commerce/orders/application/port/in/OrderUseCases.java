package com.codillas.academy.commerce.orders.application.port.in;

import java.util.List;
import java.util.UUID;

public interface OrderUseCases {

    OrderResult place(PlaceOrderCommand command);

    OrderResult get(UUID orderId);

    List<OrderResult> list();

    OrderResult confirm(UUID orderId);

    OrderResult cancel(UUID orderId);
}
