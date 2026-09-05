package com.commerceflow.inventoryservice.service;

import com.commerceflow.inventoryservice.entity.OutboxEvent;
import com.commerceflow.inventoryservice.kafka.event.InventoryConfirmedEvent;
import com.commerceflow.inventoryservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveInventoryConfirmedEvent(
            Long orderId
    ) {

        try {
            UUID eventId = UUID.randomUUID();

            InventoryConfirmedEvent event =
                    new InventoryConfirmedEvent(
                            eventId,
                            orderId
                    );

            String payload =
                    objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent =
                    OutboxEvent.builder()
                            .eventId(eventId)
                            .aggregateType("ORDER")
                            .aggregateId(orderId)
                            .eventType("INVENTORY_CONFIRMED")
                            .payload(payload)
                            .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Failed to create inventory confirmed event",
                    ex
            );
        }
    }
}