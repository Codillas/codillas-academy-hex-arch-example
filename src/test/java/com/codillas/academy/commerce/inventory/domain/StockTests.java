package com.codillas.academy.commerce.inventory.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTests {

    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");

    @Test
    void restocksReservesAndReleasesUnits() {
        var stock = Stock.empty(UUID.randomUUID(), NOW)
                .restock(5, NOW)
                .reserve(2, NOW)
                .release(1, NOW);

        assertThat(stock.availableQuantity()).isEqualTo(4);
    }

    @Test
    void preventsOverselling() {
        var stock = Stock.empty(UUID.randomUUID(), NOW).restock(2, NOW);

        assertThatThrownBy(() -> stock.reserve(3, NOW))
                .isInstanceOf(InsufficientStockDomainException.class);
    }

    @Test
    void rejectsNonPositiveStockChanges() {
        var stock = Stock.empty(UUID.randomUUID(), NOW);

        assertThatThrownBy(() -> stock.restock(0, NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be positive");
    }
}
