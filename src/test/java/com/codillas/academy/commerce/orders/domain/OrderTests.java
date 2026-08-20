package com.codillas.academy.commerce.orders.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTests {

    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");

    @Test
    void snapshotsProductDataAndCalculatesTheTotal() {
        var order = place();

        assertThat(order.productName()).isEqualTo("Mechanical keyboard");
        assertThat(order.unitPrice()).isEqualByComparingTo("129.90");
        assertThat(order.totalPrice()).isEqualByComparingTo("259.80");
        assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void supportsConfirmationAndCancellation() {
        var confirmed = place().confirm(NOW.plusSeconds(60));
        var cancelled = confirmed.cancel(NOW.plusSeconds(120));

        assertThat(confirmed.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void rejectsConfirmationAfterCancellation() {
        var cancelled = place().cancel(NOW.plusSeconds(60));

        assertThatThrownBy(() -> cancelled.confirm(NOW.plusSeconds(120)))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("cannot transition from CANCELLED to CONFIRMED");
    }

    @Test
    void makesLifecycleCommandsIdempotent() {
        var confirmed = place().confirm(NOW.plusSeconds(60));
        var cancelled = confirmed.cancel(NOW.plusSeconds(120));

        assertThat(confirmed.confirm(NOW.plusSeconds(180))).isSameAs(confirmed);
        assertThat(cancelled.cancel(NOW.plusSeconds(180))).isSameAs(cancelled);
    }

    private static Order place() {
        return Order.place(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "  Mechanical keyboard  ",
                new BigDecimal("129.9"),
                2,
                NOW
        );
    }
}
