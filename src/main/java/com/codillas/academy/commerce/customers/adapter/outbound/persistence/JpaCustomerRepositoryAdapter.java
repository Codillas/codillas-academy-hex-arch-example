package com.codillas.academy.commerce.customers.adapter.outbound.persistence;

import com.codillas.academy.commerce.customers.application.port.outbound.CustomerRepository;
import com.codillas.academy.commerce.customers.application.exception.DuplicateCustomerEmailException;
import com.codillas.academy.commerce.customers.domain.Customer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaCustomerRepositoryAdapter implements CustomerRepository {

    private final SpringDataCustomerRepository repository;

    JpaCustomerRepositoryAdapter(SpringDataCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer save(Customer customer) {
        try {
            return repository.saveAndFlush(CustomerJpaEntity.fromDomain(customer)).toDomain();
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCustomerEmailException(customer.email());
        }
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return repository.findById(customerId).map(CustomerJpaEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email).map(CustomerJpaEntity::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(CustomerJpaEntity::toDomain)
                .toList();
    }
}
