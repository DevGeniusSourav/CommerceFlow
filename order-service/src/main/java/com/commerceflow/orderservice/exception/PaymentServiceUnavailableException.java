package com.commerceflow.orderservice.exception;

public class PaymentServiceUnavailableException extends RuntimeException {
    public PaymentServiceUnavailableException() {
        super("Payment Service Is Temporarily Unavailable");
    }
}
