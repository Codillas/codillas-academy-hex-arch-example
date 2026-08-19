package com.example.hexagonalorders.order.internal.application;

import com.example.hexagonalorders.order.PlaceOrderCommand;
import com.example.hexagonalorders.order.internal.domain.Order;
import com.example.hexagonalorders.order.internal.port.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderApplicationServiceTests {

    @Test
    void placesAndReadsAnOrderThroughPorts() {
        var repository = new InMemoryOrderRepository();
        var service = new OrderApplicationService(repository);

        var placed = service.placeOrder(new PlaceOrderCommand("Mechanical keyboard", 2));

        assertThat(placed.product()).isEqualTo("Mechanical keyboard");
        assertThat(placed.quantity()).isEqualTo(2);
        assertThat(placed.status()).isEqualTo("PLACED");
        assertThat(service.findOrder(placed.id())).contains(placed);
        assertThat(service.findAllOrders()).containsExactly(placed);
    }

    private static final class InMemoryOrderRepository implements OrderRepository {

        private final LinkedHashMap<UUID, Order> orders = new LinkedHashMap<>();

        @Override
        public Order save(Order order) {
            orders.put(order.id(), order);
            return order;
        }

        @Override
        public Optional<Order> findById(UUID id) {
            return Optional.ofNullable(orders.get(id));
        }

        @Override
        public List<Order> findAll() {
            return new ArrayList<>(orders.values());
        }
    }
}
