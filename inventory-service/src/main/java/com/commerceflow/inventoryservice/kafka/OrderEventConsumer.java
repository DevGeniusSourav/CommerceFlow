package com.commerceflow.inventoryservice.kafka;

import com.commerceflow.inventoryservice.entity.ProcessedEvent;
import com.commerceflow.inventoryservice.kafka.event.OrderPaidEvent;
import com.commerceflow.inventoryservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "order-events",
            groupId = "inventory-service"
    )
    public void consume(OrderPaidEvent event) {

        System.out.println(
                "Received ORDER_PAID for order: "
                        + event.orderId()
        );

        if (processedEventRepository.existsById(event.eventId())) {
            System.out.println(
                    "Event already processed: "
                            + event.eventId()
            );
            return;
        }

        processedEventRepository.save(
                new ProcessedEvent(event.eventId())
        );
    }
}