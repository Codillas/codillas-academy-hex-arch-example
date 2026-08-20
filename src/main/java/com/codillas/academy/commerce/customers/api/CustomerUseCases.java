package com.codillas.academy.commerce.customers.api;

import java.util.List;
import java.util.UUID;

public interface CustomerUseCases {

    CustomerResult register(RegisterCustomerCommand command);

    CustomerResult get(UUID customerId);

    List<CustomerResult> list();
}
