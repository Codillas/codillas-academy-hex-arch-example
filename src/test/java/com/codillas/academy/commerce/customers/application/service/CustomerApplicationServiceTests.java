package com.codillas.academy.commerce.customers.application.service;

import com.codillas.academy.commerce.customers.api.RegisterCustomerCommand;
import com.codillas.academy.commerce.customers.application.exception.CustomerNotFoundException;
import com.codillas.academy.commerce.customers.application.exception.DuplicateCustomerEmailException;
import com.codillas.academy.commerce.customers.application.port.outbound.CustomerRepository;
import com.codillas.academy.commerce.customers.domain.Customer;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerApplicationServiceTests {

    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void registersAndQueriesCustomersThroughPorts() {
        var service = new CustomerApplicationService(new InMemoryCustomerRepository(), CLOCK);

        var registered = service.register(new RegisterCustomerCommand("Ada", "ADA@EXAMPLE.COM"));

        assertThat(service.get(registered.id())).isEqualTo(registered);
        assertThat(service.findById(registered.id())).contains(registered);
        assertThat(service.list()).containsExactly(registered);
        assertThat(registered.email()).isEqualTo("ada@example.com");
    }

    @Test
    void rejectsDuplicateEmailAddressesIgnoringCase() {
        var service = new CustomerApplicationService(new InMemoryCustomerRepository(), CLOCK);
        service.register(new RegisterCustomerCommand("Ada", "ada@example.com"));

        assertThatThrownBy(() -> service.register(
                new RegisterCustomerCommand("Another Ada", "ADA@EXAMPLE.COM")))
                .isInstanceOf(DuplicateCustomerEmailException.class);
    }

    @Test
    void reportsAnUnknownCustomer() {
        var service = new CustomerApplicationService(new InMemoryCustomerRepository(), CLOCK);

        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    private static final class InMemoryCustomerRepository implements CustomerRepository {

        private final LinkedHashMap<UUID, Customer> customers = new LinkedHashMap<>();

        @Override
        public Customer save(Customer customer) {
            customers.put(customer.id(), customer);
            return customer;
        }

        @Override
        public Optional<Customer> findById(UUID customerId) {
            return Optional.ofNullable(customers.get(customerId));
        }

        @Override
        public Optional<Customer> findByEmail(String email) {
            return customers.values().stream().filter(customer -> customer.email().equals(email)).findFirst();
        }

        @Override
        public List<Customer> findAll() {
            return new ArrayList<>(customers.values());
        }
    }
}
