package com.example.hexagonalorders.order.internal.application;

import com.example.hexagonalorders.order.GetOrdersQuery;
import com.example.hexagonalorders.order.OrderView;
import com.example.hexagonalorders.order.PlaceOrderCommand;
import com.example.hexagonalorders.order.PlaceOrderUseCase;
import com.example.hexagonalorders.order.internal.domain.Order;
import com.example.hexagonalorders.order.internal.port.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderApplicationService implements PlaceOrderUseCase, GetOrdersQuery {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderView placeOrder(PlaceOrderCommand command) {
        var order = Order.place(command.product(), command.quantity());
        return toView(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderView> findOrder(UUID id) {
        return orderRepository.findById(id).map(OrderApplicationService::toView);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderView> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderApplicationService::toView)
                .toList();
    }

    private static OrderView toView(Order order) {
        return new OrderView(
                order.id(),
                order.product(),
                order.quantity(),
                order.status().name(),
                order.createdAt()
        );
    }
}
