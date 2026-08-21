package com.codillas.academy.commerce.orders.application.port.out;

import com.codillas.academy.commerce.orders.domain.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    Optional<Order> findByIdForUpdate(UUID orderId);

    List<Order> findAll();
}
