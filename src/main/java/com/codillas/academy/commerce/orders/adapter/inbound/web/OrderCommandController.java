package com.codillas.academy.commerce.orders.adapter.inbound.web;

import com.codillas.academy.commerce.orders.api.CancelOrderUseCase;
import com.codillas.academy.commerce.orders.api.ConfirmOrderUseCase;
import com.codillas.academy.commerce.orders.api.PlaceOrderCommand;
import com.codillas.academy.commerce.orders.api.PlaceOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
class OrderCommandController {

    private final PlaceOrderUseCase placeOrder;
    private final ConfirmOrderUseCase confirmOrder;
    private final CancelOrderUseCase cancelOrder;

    OrderCommandController(
            PlaceOrderUseCase placeOrder,
            ConfirmOrderUseCase confirmOrder,
            CancelOrderUseCase cancelOrder
    ) {
        this.placeOrder = placeOrder;
        this.confirmOrder = confirmOrder;
        this.cancelOrder = cancelOrder;
    }

    @PostMapping
    ResponseEntity<OrderResponse> place(@Valid @RequestBody PlaceOrderRequest request) {
        var result = placeOrder.place(new PlaceOrderCommand(
                request.customerId(),
                request.productId(),
                request.quantity()
        ));
        return ResponseEntity.created(URI.create("/api/orders/" + result.id()))
                .body(OrderResponse.from(result));
    }

    @PutMapping("/{orderId}/confirmation")
    OrderResponse confirm(@PathVariable UUID orderId) {
        return OrderResponse.from(confirmOrder.confirm(orderId));
    }

    @PutMapping("/{orderId}/cancellation")
    OrderResponse cancel(@PathVariable UUID orderId) {
        return OrderResponse.from(cancelOrder.cancel(orderId));
    }
}
