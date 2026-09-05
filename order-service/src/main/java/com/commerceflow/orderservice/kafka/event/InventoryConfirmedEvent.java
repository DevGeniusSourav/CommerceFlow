package com.commerceflow.orderservice.kafka.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InventoryConfirmedEvent(
        UUID eventId,
        Long orderId,
        Long customerId,
        BigDecimal totalAmount
) {
}
