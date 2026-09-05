package com.commerceflow.orderservice.kafka.event;

import com.commerceflow.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-events",
            groupId = "order-service"
    )
    public void consume(String payload) {

        try {
            InventoryConfirmedEvent event =
                    objectMapper.readValue(payload, InventoryConfirmedEvent.class);

            orderService.handleInventoryConfirmed(event);

        } catch (Exception ex) {
            // Do not rethrow: a malformed / unrelated message must not
            // block the partition or trigger an endless redelivery loop.
            log.error("Failed to process order-events message: {}", payload, ex);
        }
    }
}
