package com.commerceflow.inventoryservice.exception;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(Long productId, Integer quantity) {
        super("Requested quantity: " + quantity + ", not available for product id: " + productId);
    }
}
