package com.commerceflow.paymentservice.exception;

import com.commerceflow.paymentservice.enums.PaymentStatus;

public class InvalidPaymentStateException extends RuntimeException {
    public InvalidPaymentStateException(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        super("Cannot transition payment from " + currentStatus + " to " + targetStatus
                + "; only payments in " + PaymentStatus.PENDING + " state can be updated");
    }
}
