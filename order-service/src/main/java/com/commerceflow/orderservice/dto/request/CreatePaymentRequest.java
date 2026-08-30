package com.commerceflow.orderservice.dto.request;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        Long orderId,
        BigDecimal amount
) {
}
