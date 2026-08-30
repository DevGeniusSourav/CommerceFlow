package com.commerceflow.inventoryservice.exception;

public class InventoryReservationAlreadyExists extends RuntimeException {
    public InventoryReservationAlreadyExists(Long orderId, Long productId) {
        super("Inventory Already Reserved for order id: " + orderId + " and product id: " + productId);
    }
}
