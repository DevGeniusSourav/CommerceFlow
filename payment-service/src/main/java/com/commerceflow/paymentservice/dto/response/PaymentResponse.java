package com.commerceflow.paymentservice.dto.response;

import com.commerceflow.paymentservice.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status
) {
}