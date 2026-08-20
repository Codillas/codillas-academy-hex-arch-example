package com.codillas.academy.commerce.customers.api;

import java.util.UUID;

public interface GetCustomerUseCase {

    CustomerResult get(UUID customerId);
}
