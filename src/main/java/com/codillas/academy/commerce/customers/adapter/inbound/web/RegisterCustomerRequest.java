package com.codillas.academy.commerce.customers.adapter.inbound.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RegisterCustomerRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 320) String email
) {
}
