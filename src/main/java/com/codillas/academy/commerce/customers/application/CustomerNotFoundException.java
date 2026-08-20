package com.codillas.academy.commerce.customers.application;

import java.util.UUID;

public final class CustomerNotFoundException extends RuntimeException {

    private final UUID customerId;

    public CustomerNotFoundException(UUID customerId) {
        super("Customer %s was not found".formatted(customerId));
        this.customerId = customerId;
    }

    public UUID customerId() {
        return customerId;
    }
}
