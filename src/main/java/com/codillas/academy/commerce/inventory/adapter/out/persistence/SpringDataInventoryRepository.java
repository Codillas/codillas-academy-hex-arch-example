package com.codillas.academy.commerce.inventory.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface SpringDataInventoryRepository extends JpaRepository<InventoryJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select inventory from InventoryJpaEntity inventory where inventory.productId = :productId")
    Optional<InventoryJpaEntity> findByProductIdForUpdate(@Param("productId") UUID productId);
}
