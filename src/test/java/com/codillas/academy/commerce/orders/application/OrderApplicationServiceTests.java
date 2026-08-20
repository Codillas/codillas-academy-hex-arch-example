package com.codillas.academy.commerce.orders.application;

import com.codillas.academy.commerce.catalog.api.ProductCatalog;
import com.codillas.academy.commerce.catalog.api.ProductResult;
import com.codillas.academy.commerce.customers.api.CustomerDirectory;
import com.codillas.academy.commerce.customers.api.CustomerResult;
import com.codillas.academy.commerce.inventory.api.InsufficientStockException;
import com.codillas.academy.commerce.inventory.api.InventoryOperations;
import com.codillas.academy.commerce.inventory.api.InventoryResult;
import com.codillas.academy.commerce.orders.api.PlaceOrderCommand;
import com.codillas.academy.commerce.orders.domain.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderApplicationServiceTests {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final CustomerResult CUSTOMER = new CustomerResult(
            CUSTOMER_ID, "Ada", "ada@example.com", NOW);
    private static final ProductResult PRODUCT = new ProductResult(
            PRODUCT_ID, "Keyboard", new BigDecimal("129.90"), true, NOW, NOW);

    @Test
    void coordinatesTheCompleteOrderLifecycleAcrossModuleApis() {
        var inventory = new InMemoryInventory(5);
        var service = service(
                customerId -> Optional.of(CUSTOMER),
                productId -> Optional.of(PRODUCT),
                inventory
        );

        var placed = service.place(new PlaceOrderCommand(CUSTOMER_ID, PRODUCT_ID, 2));
        var confirmed = service.confirm(placed.id());
        var cancelled = service.cancel(placed.id());
        var cancelledAgain = service.cancel(placed.id());

        assertThat(placed.status()).isEqualTo("PLACED");
        assertThat(placed.totalPrice()).isEqualByComparingTo("259.80");
        assertThat(confirmed.status()).isEqualTo("CONFIRMED");
        assertThat(cancelled.status()).isEqualTo("CANCELLED");
        assertThat(cancelledAgain).isEqualTo(cancelled);
        assertThat(inventory.available).isEqualTo(5);
        assertThat(inventory.releaseCalls).isEqualTo(1);
        assertThat(service.get(placed.id())).isEqualTo(cancelled);
        assertThat(service.list()).containsExactly(cancelled);
    }

    @Test
    void rejectsAnUnknownCustomerBeforeTouchingInventory() {
        var inventory = new InMemoryInventory(5);
        var service = service(
                customerId -> Optional.empty(),
                productId -> Optional.of(PRODUCT),
                inventory
        );

        assertThatThrownBy(() -> service.place(new PlaceOrderCommand(CUSTOMER_ID, PRODUCT_ID, 1)))
                .isInstanceOf(OrderPrerequisiteException.class)
                .hasMessageContaining("Customer");
        assertThat(inventory.reserveCalls).isZero();
    }

    @Test
    void rejectsAnInactiveOrUnknownProduct() {
        var inventory = new InMemoryInventory(5);
        var service = service(
                customerId -> Optional.of(CUSTOMER),
                productId -> Optional.empty(),
                inventory
        );

        assertThatThrownBy(() -> service.place(new PlaceOrderCommand(CUSTOMER_ID, PRODUCT_ID, 1)))
                .isInstanceOf(OrderPrerequisiteException.class)
                .hasMessageContaining("Product");
        assertThat(inventory.reserveCalls).isZero();
    }

    @Test
    void propagatesTheInventoryModulesPublicStockError() {
        var service = service(
                customerId -> Optional.of(CUSTOMER),
                productId -> Optional.of(PRODUCT),
                new InMemoryInventory(1)
        );

        assertThatThrownBy(() -> service.place(new PlaceOrderCommand(CUSTOMER_ID, PRODUCT_ID, 2)))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void reportsAnUnknownOrder() {
        var service = service(
                customerId -> Optional.of(CUSTOMER),
                productId -> Optional.of(PRODUCT),
                new InMemoryInventory(5)
        );

        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private static OrderApplicationService service(
            CustomerDirectory customers,
            ProductCatalog products,
            InventoryOperations inventory
    ) {
        return new OrderApplicationService(
                new InMemoryOrderRepository(),
                customers,
                products,
                inventory,
                CLOCK
        );
    }

    private static final class InMemoryInventory implements InventoryOperations {

        private int available;
        private int reserveCalls;
        private int releaseCalls;

        private InMemoryInventory(int available) {
            this.available = available;
        }

        @Override
        public InventoryResult reserve(UUID productId, int quantity) {
            reserveCalls++;
            if (quantity > available) {
                throw new InsufficientStockException(productId, quantity, available);
            }
            available -= quantity;
            return result(productId);
        }

        @Override
        public InventoryResult release(UUID productId, int quantity) {
            releaseCalls++;
            available += quantity;
            return result(productId);
        }

        private InventoryResult result(UUID productId) {
            return new InventoryResult(productId, available, NOW);
        }
    }

    private static final class InMemoryOrderRepository implements OrderRepository {

        private final LinkedHashMap<UUID, Order> orders = new LinkedHashMap<>();

        @Override
        public Order save(Order order) {
            orders.put(order.id(), order);
            return order;
        }

        @Override
        public Optional<Order> findById(UUID orderId) {
            return Optional.ofNullable(orders.get(orderId));
        }

        @Override
        public Optional<Order> findByIdForUpdate(UUID orderId) {
            return findById(orderId);
        }

        @Override
        public List<Order> findAll() {
            return new ArrayList<>(orders.values());
        }
    }
}
