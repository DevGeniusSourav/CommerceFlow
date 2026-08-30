package com.commerceflow.inventoryservice.exception;

public class InvalidReservationStateException extends RuntimeException {
    public InvalidReservationStateException() {
        super("Only RESERVED reservations can be confirmed.");
    }
}
