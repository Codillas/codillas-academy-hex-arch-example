package com.codillas.academy.commerce.catalog.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {

    List<ProductJpaEntity> findAllByOrderByCreatedAtDesc();
}
