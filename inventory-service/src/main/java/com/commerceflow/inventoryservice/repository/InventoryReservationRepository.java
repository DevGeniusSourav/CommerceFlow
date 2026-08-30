package com.commerceflow.inventoryservice.repository;

import com.commerceflow.inventoryservice.entity.InventoryReservation;
import com.commerceflow.inventoryservice.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    Optional<InventoryReservation> findByOrderIdAndProductId(Long orderId, Long productid);

    Optional<InventoryReservation> findByOrderIdAndProductIdAndStatus(Long orderId, Long productId, ReservationStatus status);

    List<InventoryReservation> findAllByOrderIdAndStatus(
            Long orderId,
            ReservationStatus status
    );
}
