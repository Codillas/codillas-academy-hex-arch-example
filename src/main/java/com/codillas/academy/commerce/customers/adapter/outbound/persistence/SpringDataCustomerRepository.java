package com.codillas.academy.commerce.customers.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {

    Optional<CustomerJpaEntity> findByEmail(String email);

    List<CustomerJpaEntity> findAllByOrderByCreatedAtDesc();
}
