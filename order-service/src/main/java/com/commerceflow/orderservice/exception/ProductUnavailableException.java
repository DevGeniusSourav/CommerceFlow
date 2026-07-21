package com.commerceflow.orderservice.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(Long id) {
        super("Product " + id + " is unavailable");
    }
}
