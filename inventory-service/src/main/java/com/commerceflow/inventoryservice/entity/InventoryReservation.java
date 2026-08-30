package com.commerceflow.inventoryservice.entity;

import com.commerceflow.inventoryservice.enums.ReservationStatus;
import com.commerceflow.inventoryservice.exception.InvalidReservationStateException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_reservations")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InventoryReservation {

    @Id
    @GeneratedValue
    private Long id;

    private Long orderId;

    private Long productId;

    private Long inventoryId;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Instant createdAt;

    private Instant expiresAt;

    public void confirm() {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidReservationStateException();
        }

        status = ReservationStatus.CONFIRMED;
    }

    public void release() {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidReservationStateException();
        }

        status = ReservationStatus.RELEASED;
    }
}