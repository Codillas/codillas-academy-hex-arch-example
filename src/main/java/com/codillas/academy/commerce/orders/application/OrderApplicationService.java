package com.codillas.academy.commerce.orders.application;

import com.codillas.academy.commerce.catalog.api.ProductCatalog;
import com.codillas.academy.commerce.customers.api.CustomerDirectory;
import com.codillas.academy.commerce.inventory.api.InventoryOperations;
import com.codillas.academy.commerce.orders.api.OrderResult;
import com.codillas.academy.commerce.orders.api.OrderUseCases;
import com.codillas.academy.commerce.orders.api.PlaceOrderCommand;
import com.codillas.academy.commerce.orders.domain.Order;
import com.codillas.academy.commerce.orders.domain.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrderApplicationService implements OrderUseCases {

    private final OrderRepository orderRepository;
    private final CustomerDirectory customerDirectory;
    private final ProductCatalog productCatalog;
    private final InventoryOperations inventoryOperations;
    private final Clock clock;

    public OrderApplicationService(
            OrderRepository orderRepository,
            CustomerDirectory customerDirectory,
            ProductCatalog productCatalog,
            InventoryOperations inventoryOperations,
            Clock clock
    ) {
        this.orderRepository = orderRepository;
        this.customerDirectory = customerDirectory;
        this.productCatalog = productCatalog;
        this.inventoryOperations = inventoryOperations;
        this.clock = clock;
    }

    @Override
    @Transactional
    public OrderResult place(PlaceOrderCommand command) {
        customerDirectory.findById(command.customerId())
                .orElseThrow(() -> new OrderPrerequisiteException(
                        "Customer %s does not exist".formatted(command.customerId())));
        var product = productCatalog.findAvailableById(command.productId())
                .orElseThrow(() -> new OrderPrerequisiteException(
                        "Product %s does not exist or is inactive".formatted(command.productId())));

        inventoryOperations.reserve(product.id(), command.quantity());
        var order = Order.place(
                command.customerId(),
                product.id(),
                product.name(),
                product.price(),
                command.quantity(),
                now()
        );
        return toResult(orderRepository.save(order));
    }

    @Override
    public OrderResult get(UUID orderId) {
        return toResult(load(orderId));
    }

    @Override
    public List<OrderResult> list() {
        return orderRepository.findAll().stream()
                .map(OrderApplicationService::toResult)
                .toList();
    }

    @Override
    @Transactional
    public OrderResult confirm(UUID orderId) {
        return toResult(orderRepository.save(loadForUpdate(orderId).confirm(now())));
    }

    @Override
    @Transactional
    public OrderResult cancel(UUID orderId) {
        var order = loadForUpdate(orderId);
        if (order.status() == OrderStatus.CANCELLED) {
            return toResult(order);
        }
        var cancelled = order.cancel(now());
        inventoryOperations.release(order.productId(), order.quantity());
        return toResult(orderRepository.save(cancelled));
    }

    private Order load(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Order loadForUpdate(UUID orderId) {
        return orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private static OrderResult toResult(Order order) {
        return new OrderResult(
                order.id(),
                order.customerId(),
                order.productId(),
                order.productName(),
                order.unitPrice(),
                order.quantity(),
                order.totalPrice(),
                order.status().name(),
                order.createdAt(),
                order.updatedAt()
        );
    }
}
