package com.example.hexagonalorders.order;

public interface PlaceOrderUseCase {

    OrderView placeOrder(PlaceOrderCommand command);
}
