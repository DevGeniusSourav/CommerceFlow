package com.commerceflow.orderservice.kafka;

import com.commerceflow.orderservice.entity.OutboxEvent;
import com.commerceflow.orderservice.enums.OutboxEventStatus;
import com.commerceflow.orderservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final String TOPIC = "order-events";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop100ByStatusOrderByCreatedAtAsc(
                                OutboxEventStatus.PENDING
                        );

        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {

        try {
            kafkaTemplate
                    .send(
                            TOPIC,
                            event.getAggregateId().toString(),
                            event.getPayload()
                    )
                    .get();

            event.markPublished();
            outboxEventRepository.save(event);

        } catch (Exception ex) {
            // Leave the event as PENDING.
            // The next scheduled execution will retry it.
        }
    }
}
