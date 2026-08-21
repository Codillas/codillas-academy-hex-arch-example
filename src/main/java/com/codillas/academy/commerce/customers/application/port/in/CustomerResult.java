package com.codillas.academy.commerce.customers.application.port.in;

import java.time.Instant;
import java.util.UUID;

public record CustomerResult(
        UUID id,
        String name,
        String email,
        Instant createdAt
) {
}
