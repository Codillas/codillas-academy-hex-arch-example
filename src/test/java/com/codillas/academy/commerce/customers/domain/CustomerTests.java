package com.codillas.academy.commerce.customers.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTests {

    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");

    @Test
    void normalizesRegistrationData() {
        var customer = Customer.register("  Ada Lovelace  ", "  ADA@EXAMPLE.COM  ", NOW);

        assertThat(customer.name()).isEqualTo("Ada Lovelace");
        assertThat(customer.email()).isEqualTo("ada@example.com");
        assertThat(customer.createdAt()).isEqualTo(NOW);
    }

    @Test
    void rejectsAnInvalidEmail() {
        assertThatThrownBy(() -> Customer.register("Ada", "not-an-email", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email must be a valid address");
    }
}
