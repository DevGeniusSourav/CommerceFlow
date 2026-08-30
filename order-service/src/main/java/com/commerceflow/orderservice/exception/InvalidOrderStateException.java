package com.commerceflow.orderservice.exception;

import com.commerceflow.orderservice.enums.OrderStatus;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(OrderStatus currentStatus, OrderStatus targetStatus) {
        super("Cannot transition order from " + currentStatus + " to " + targetStatus
                + "; order must be in " + OrderStatus.PENDING_PAYMENT + " state");
    }
}
