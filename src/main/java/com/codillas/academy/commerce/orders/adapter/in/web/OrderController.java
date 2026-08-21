package com.codillas.academy.commerce.orders.adapter.in.web;

import com.codillas.academy.commerce.orders.application.port.in.OrderUseCases;
import com.codillas.academy.commerce.orders.application.port.in.PlaceOrderCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderUseCases orders;

    OrderController(OrderUseCases orders) {
        this.orders = orders;
    }

    @PostMapping
    ResponseEntity<OrderResponse> place(@Valid @RequestBody PlaceOrderRequest request) {
        var result = orders.place(new PlaceOrderCommand(
                request.customerId(),
                request.productId(),
                request.quantity()
        ));
        return ResponseEntity.created(URI.create("/api/orders/" + result.id()))
                .body(OrderResponse.from(result));
    }

    @PutMapping("/{orderId}/confirmation")
    OrderResponse confirm(@PathVariable UUID orderId) {
        return OrderResponse.from(orders.confirm(orderId));
    }

    @PutMapping("/{orderId}/cancellation")
    OrderResponse cancel(@PathVariable UUID orderId) {
        return OrderResponse.from(orders.cancel(orderId));
    }

    @GetMapping("/{orderId}")
    OrderResponse get(@PathVariable UUID orderId) {
        return OrderResponse.from(orders.get(orderId));
    }

    @GetMapping
    List<OrderResponse> list() {
        return orders.list().stream().map(OrderResponse::from).toList();
    }
}
