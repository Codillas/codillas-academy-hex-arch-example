package com.codillas.academy.commerce.catalog.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTests {

    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");

    @Test
    void createsAndDeactivatesAProduct() {
        var product = Product.create("  Mechanical keyboard  ", new BigDecimal("129.9"), NOW);
        var deactivated = product.deactivate(NOW.plusSeconds(60));

        assertThat(product.name()).isEqualTo("Mechanical keyboard");
        assertThat(product.price()).isEqualByComparingTo("129.90");
        assertThat(product.active()).isTrue();
        assertThat(deactivated.active()).isFalse();
        assertThat(deactivated.updatedAt()).isEqualTo(NOW.plusSeconds(60));
    }

    @Test
    void rejectsUnsupportedPricePrecision() {
        assertThatThrownBy(() -> Product.create("Keyboard", new BigDecimal("129.999"), NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price must have at most two decimal places");
    }

    @Test
    void makesDeactivationIdempotent() {
        var deactivated = Product.create("Keyboard", new BigDecimal("129.00"), NOW)
                .deactivate(NOW.plusSeconds(60));

        assertThat(deactivated.deactivate(NOW.plusSeconds(120))).isSameAs(deactivated);
    }
}
