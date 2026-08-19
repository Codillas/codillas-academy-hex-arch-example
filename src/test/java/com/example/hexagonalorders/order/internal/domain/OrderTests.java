package com.example.hexagonalorders.order.internal.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTests {

    @Test
    void normalizesProductWhenPlacingAnOrder() {
        var order = Order.place("  Mechanical keyboard  ", 2);

        assertThat(order.product()).isEqualTo("Mechanical keyboard");
        assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> Order.place("Mechanical keyboard", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be positive");
    }
}
