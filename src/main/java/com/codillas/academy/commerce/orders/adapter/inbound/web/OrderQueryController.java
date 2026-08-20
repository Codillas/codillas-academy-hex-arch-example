package com.codillas.academy.commerce.orders.adapter.inbound.web;

import com.codillas.academy.commerce.orders.api.GetOrderUseCase;
import com.codillas.academy.commerce.orders.api.ListOrdersUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
class OrderQueryController {

    private final GetOrderUseCase getOrder;
    private final ListOrdersUseCase listOrders;

    OrderQueryController(GetOrderUseCase getOrder, ListOrdersUseCase listOrders) {
        this.getOrder = getOrder;
        this.listOrders = listOrders;
    }

    @GetMapping("/{orderId}")
    OrderResponse get(@PathVariable UUID orderId) {
        return OrderResponse.from(getOrder.get(orderId));
    }

    @GetMapping
    List<OrderResponse> list() {
        return listOrders.list().stream().map(OrderResponse::from).toList();
    }
}
