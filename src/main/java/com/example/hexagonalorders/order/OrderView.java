package com.example.hexagonalorders.order;

import java.time.Instant;
import java.util.UUID;

public record OrderView(
        UUID id,
        String product,
        int quantity,
        String status,
        Instant createdAt
) {
}
