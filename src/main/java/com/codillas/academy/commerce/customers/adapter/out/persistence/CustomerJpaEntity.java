package com.codillas.academy.commerce.customers.adapter.out.persistence;

import com.codillas.academy.commerce.customers.domain.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
class CustomerJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 320, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CustomerJpaEntity() {
    }

    private CustomerJpaEntity(UUID id, String name, String email, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    static CustomerJpaEntity fromDomain(Customer customer) {
        return new CustomerJpaEntity(
                customer.id(),
                customer.name(),
                customer.email(),
                customer.createdAt()
        );
    }

    Customer toDomain() {
        return new Customer(id, name, email, createdAt);
    }
}
