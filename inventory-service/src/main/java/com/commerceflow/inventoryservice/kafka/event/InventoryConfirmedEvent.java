package com.commerceflow.inventoryservice.kafka.event;

import java.util.UUID;

public record InventoryConfirmedEvent(
        UUID eventId,
        Long orderId
) {
}