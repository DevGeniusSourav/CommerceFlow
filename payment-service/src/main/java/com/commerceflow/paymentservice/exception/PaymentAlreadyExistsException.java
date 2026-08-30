package com.commerceflow.paymentservice.exception;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(Long orderId) {
        super("Payment already exists for order id: " + orderId);
    }
}
