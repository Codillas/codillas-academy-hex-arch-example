package com.codillas.academy.commerce.customers.application.service;

import com.codillas.academy.commerce.customers.api.CustomerDirectory;
import com.codillas.academy.commerce.customers.api.CustomerResult;
import com.codillas.academy.commerce.customers.api.GetCustomerUseCase;
import com.codillas.academy.commerce.customers.api.ListCustomersUseCase;
import com.codillas.academy.commerce.customers.api.RegisterCustomerCommand;
import com.codillas.academy.commerce.customers.api.RegisterCustomerUseCase;
import com.codillas.academy.commerce.customers.application.exception.CustomerNotFoundException;
import com.codillas.academy.commerce.customers.application.exception.DuplicateCustomerEmailException;
import com.codillas.academy.commerce.customers.application.port.outbound.CustomerRepository;
import com.codillas.academy.commerce.customers.domain.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CustomerApplicationService implements
        RegisterCustomerUseCase,
        GetCustomerUseCase,
        ListCustomersUseCase,
        CustomerDirectory {

    private final CustomerRepository customerRepository;
    private final Clock clock;

    public CustomerApplicationService(CustomerRepository customerRepository, Clock clock) {
        this.customerRepository = customerRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CustomerResult register(RegisterCustomerCommand command) {
        var normalizedEmail = command.email().strip().toLowerCase(Locale.ROOT);
        customerRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new DuplicateCustomerEmailException(normalizedEmail);
        });
        var customer = Customer.register(command.name(), normalizedEmail, now());
        return toResult(customerRepository.save(customer));
    }

    @Override
    public CustomerResult get(UUID customerId) {
        return findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    @Override
    public Optional<CustomerResult> findById(UUID customerId) {
        return customerRepository.findById(customerId).map(CustomerApplicationService::toResult);
    }

    @Override
    public List<CustomerResult> list() {
        return customerRepository.findAll().stream()
                .map(CustomerApplicationService::toResult)
                .toList();
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private static CustomerResult toResult(Customer customer) {
        return new CustomerResult(
                customer.id(),
                customer.name(),
                customer.email(),
                customer.createdAt()
        );
    }
}
