package com.commerceflow.orderservice.dto.response;

import com.commerceflow.orderservice.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status
) {
}
