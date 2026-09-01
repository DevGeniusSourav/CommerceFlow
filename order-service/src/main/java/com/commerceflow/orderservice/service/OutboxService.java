package com.commerceflow.orderservice.service;

import com.commerceflow.orderservice.kafka.event.OrderPaidEvent;
import com.commerceflow.orderservice.entity.Order;
import com.commerceflow.orderservice.entity.OutboxEvent;
import com.commerceflow.orderservice.repository.OutboxEventRepository;
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

    public void saveOrderPaidEvent(Order order) {

        try {
            UUID eventId = UUID.randomUUID();

            OrderPaidEvent event = new OrderPaidEvent(
                    eventId,
                    order.getId(),
                    order.getCustomerId(),
                    order.getTotalAmount()
            );

            String payload =
                    objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(eventId)
                    .aggregateType("ORDER")
                    .aggregateId(order.getId())
                    .eventType("ORDER_PAID")
                    .payload(payload)
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Failed to create order paid event",
                    ex
            );
        }
    }
}
