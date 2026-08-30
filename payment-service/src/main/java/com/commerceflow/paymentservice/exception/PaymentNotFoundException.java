package com.commerceflow.paymentservice.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long orderId) {
        super("Payment not found with order id: " + orderId);
    }
}
