package com.codillas.academy.commerce.customers.application.port.in;

import java.util.Optional;
import java.util.UUID;

/** Public lookup contract for other business modules. */
public interface CustomerDirectory {

    Optional<CustomerResult> findById(UUID customerId);
}
