package com.codillas.academy.commerce.customers.api;

public interface RegisterCustomerUseCase {

    CustomerResult register(RegisterCustomerCommand command);
}
