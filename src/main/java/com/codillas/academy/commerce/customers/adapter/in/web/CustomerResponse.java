package com.codillas.academy.commerce.customers.adapter.in.web;

import com.codillas.academy.commerce.customers.application.port.in.CustomerResult;

import java.time.Instant;
import java.util.UUID;

record CustomerResponse(UUID id, String name, String email, Instant createdAt) {

    static CustomerResponse from(CustomerResult result) {
        return new CustomerResponse(result.id(), result.name(), result.email(), result.createdAt());
    }
}
