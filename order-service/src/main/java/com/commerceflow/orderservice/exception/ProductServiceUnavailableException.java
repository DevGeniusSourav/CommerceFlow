package com.commerceflow.orderservice.exception;

public class ProductServiceUnavailableException extends RuntimeException {
    public ProductServiceUnavailableException() {
        super("Product Service Is Temporarily Unavailable");
    }
}
