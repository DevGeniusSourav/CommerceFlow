package com.commerceflow.inventoryservice.kafka;

import com.commerceflow.inventoryservice.kafka.event.PaymentSucceededEvent;
import com.commerceflow.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventConsumer.class);

    private final InventoryService inventoryService;

    /**
     * Consumes PAYMENT_SUCCEEDED events.
     *
     * Failure handling is delegated to the container's error handler
     * (see KafkaConsumerConfig): transient failures are retried, and
     * repeated failures are routed to the "order-events.DLT" topic.
     * On success the record offset is committed (AckMode.RECORD).
     *
     * Duplicate deliveries are safely ignored inside
     * InventoryService#handlePaymentSucceeded via ProcessedEventRepository.
     */
    @KafkaListener(
            topics = "order-events",
            groupId = "inventory-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(PaymentSucceededEvent event) {
        log.info("Received PAYMENT_SUCCEEDED for order: {}", event.orderId());

        inventoryService.handlePaymentSucceeded(event);
    }
}
