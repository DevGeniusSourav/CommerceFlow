package com.commerceflow.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    private Integer totalQuantity;

    private Integer reservedQuantity;

    private Integer soldQuantity;

    private Instant createdAt;

    private Instant updatedAt;

    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity;
    }

    public boolean reserve(int requestedQuantity) {

        if (getAvailableQuantity() < requestedQuantity) {
            return false;
        }

        reservedQuantity += requestedQuantity;
        return true;
    }

    public void release(int requestedQuantity) {
        reservedQuantity -= requestedQuantity;
    }

    public void confirm(int requestedQuantity) {
        reservedQuantity -= requestedQuantity;
        totalQuantity -= requestedQuantity;
        soldQuantity += requestedQuantity;
    }

    public void restock(int quantity) {
        this.totalQuantity += quantity;
        this.updatedAt = Instant.now();
    }

}

