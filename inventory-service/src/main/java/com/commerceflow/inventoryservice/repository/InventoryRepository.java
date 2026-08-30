package com.commerceflow.inventoryservice.repository;

import com.commerceflow.inventoryservice.entity.Inventory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.productId = :productId
            ORDER BY i.id
            """)
    List<Inventory> findByProductIdForUpdate(@Param("productId") Long productId);

    List<Inventory> findByProductId(@Param("productId") Long productId);
}
