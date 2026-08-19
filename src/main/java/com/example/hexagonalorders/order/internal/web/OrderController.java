package com.example.hexagonalorders.order.internal.web;

import com.example.hexagonalorders.order.GetOrdersQuery;
import com.example.hexagonalorders.order.OrderView;
import com.example.hexagonalorders.order.PlaceOrderCommand;
import com.example.hexagonalorders.order.PlaceOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrder;
    private final GetOrdersQuery getOrders;

    public OrderController(PlaceOrderUseCase placeOrder, GetOrdersQuery getOrders) {
        this.placeOrder = placeOrder;
        this.getOrders = getOrders;
    }

    @PostMapping
    public ResponseEntity<OrderView> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        var order = placeOrder.placeOrder(new PlaceOrderCommand(request.product(), request.quantity()));
        return ResponseEntity
                .created(URI.create("/api/orders/" + order.id()))
                .body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderView> findOrder(@PathVariable UUID id) {
        return getOrders.findOrder(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<OrderView> findAllOrders() {
        return getOrders.findAllOrders();
    }
}
