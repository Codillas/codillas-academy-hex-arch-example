package com.codillas.academy.commerce.customers.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public record Customer(
        UUID id,
        String name,
        String email,
        Instant createdAt
) {

    public Customer {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");

        name = name.strip();
        email = email.strip().toLowerCase(Locale.ROOT);
        if (name.isEmpty() || name.length() > 120) {
            throw new IllegalArgumentException("name must contain between 1 and 120 characters");
        }
        if (email.isEmpty() || email.length() > 320 || !email.contains("@")) {
            throw new IllegalArgumentException("email must be a valid address");
        }
    }

    public static Customer register(String name, String email, Instant now) {
        return new Customer(UUID.randomUUID(), name, email, now);
    }
}
