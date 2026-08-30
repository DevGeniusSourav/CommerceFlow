package com.commerceflow.orderservice.exception;

public class InventoryServiceUnavailableException extends RuntimeException{
    public InventoryServiceUnavailableException() {
        super("Inventory Service Is Temporarily Unavailable");
    }
}
