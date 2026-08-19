package com.example.hexagonalorders.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetOrdersQuery {

    Optional<OrderView> findOrder(UUID id);

    List<OrderView> findAllOrders();
}
