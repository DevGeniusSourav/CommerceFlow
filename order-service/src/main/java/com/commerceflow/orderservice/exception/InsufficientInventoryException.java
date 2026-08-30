package com.commerceflow.orderservice.exception;

public class InsufficientInventoryException extends RuntimeException {

    private final Long productId;
    private final Integer requestedQuantity;

    public InsufficientInventoryException(Long productId, Integer requestedQuantity) {
        super("Requested quantity: " + requestedQuantity + ", not available for product id: " + productId);
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
    }

    // Fallback when structured data isn't available from downstream
    public InsufficientInventoryException(String message) {
        super(message);
        this.productId = null;
        this.requestedQuantity = null;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
}