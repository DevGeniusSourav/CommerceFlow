package com.commerceflow.orderservice.kafka.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentSucceededEvent(
        UUID eventId,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount
) {}