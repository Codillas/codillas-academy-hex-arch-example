package com.codillas.academy.commerce.customers.application.port.in;

import java.util.Objects;

public record RegisterCustomerCommand(String name, String email) {

    public RegisterCustomerCommand {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
    }
}
