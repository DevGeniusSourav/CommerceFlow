package com.commerceflow.inventoryservice.kafka.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderPaidEvent(
        UUID eventId,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount
) {
}
