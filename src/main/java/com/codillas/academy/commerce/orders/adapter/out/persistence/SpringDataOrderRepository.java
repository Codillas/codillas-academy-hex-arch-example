package com.codillas.academy.commerce.orders.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select orders from OrderJpaEntity orders where orders.id = :orderId")
    Optional<OrderJpaEntity> findByIdForUpdate(@Param("orderId") UUID orderId);

    List<OrderJpaEntity> findAllByOrderByCreatedAtDesc();
}
