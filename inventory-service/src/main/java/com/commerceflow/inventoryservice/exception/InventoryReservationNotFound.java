package com.commerceflow.inventoryservice.exception;

public class InventoryReservationNotFound extends RuntimeException {
    public InventoryReservationNotFound(Long orderId) {
        super("Could not find inventory reservation with orderId " + orderId);
    }
    public InventoryReservationNotFound(Long orderId, Long productId) {
        super("Could not find inventory reservation with orderId " + orderId  + " and productId " + productId);
    }
}
