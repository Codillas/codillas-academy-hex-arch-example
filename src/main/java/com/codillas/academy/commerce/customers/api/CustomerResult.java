package com.codillas.academy.commerce.customers.api;

import java.time.Instant;
import java.util.UUID;

public record CustomerResult(
        UUID id,
        String name,
        String email,
        Instant createdAt
) {
}
