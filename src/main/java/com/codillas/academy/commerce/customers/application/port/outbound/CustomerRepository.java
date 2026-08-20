package com.codillas.academy.commerce.customers.application.port.outbound;

import com.codillas.academy.commerce.customers.domain.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID customerId);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();
}
