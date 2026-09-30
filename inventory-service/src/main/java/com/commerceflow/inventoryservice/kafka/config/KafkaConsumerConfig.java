package com.commerceflow.inventoryservice.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration for retry + dead-letter handling.
 *
 * Behaviour:
 *  - Temporary failure  -> the record is retried automatically (fixed backoff).
 *  - Repeated failure   -> the record is published to a dead-letter topic ("<topic>.DLT").
 *  - Successful process -> the record offset is acknowledged (AckMode.RECORD).
 *  - Non-retryable errors (e.g. deserialization) skip retries and go straight to the DLT.
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.listener.auto-startup:true}")
    private boolean autoStartup;

    private static final Logger log =
            LoggerFactory.getLogger(KafkaConsumerConfig.class);

    /** Number of retry attempts after the first failed delivery. */
    private static final long RETRY_ATTEMPTS = 3L;

    /** Delay between retry attempts, in milliseconds. */
    private static final long RETRY_INTERVAL_MS = 2000L;

    private final String bootstrapServers;

    public KafkaConsumerConfig(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * The default String-based template used by producers such as OutboxPublisher.
     *
     * Defining any KafkaTemplate bean (see deadLetterKafkaTemplate) switches off
     * Spring Boot's auto-configured default template, so we recreate it explicitly
     * here and mark it @Primary so it is injected wherever a KafkaTemplate is needed.
     */
    @Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<String, String> factory =
                new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(factory);
    }

    /**
     * Dedicated producer used only by the dead-letter recoverer.
     *
     * The value the recoverer forwards depends on how the record failed:
     *  - A record that failed *after* deserialization (e.g. a business error in
     *    the listener) is still a live event object, so we serialize it as JSON.
     *  - A record that failed *during* deserialization (poison message) is
     *    forwarded by DeadLetterPublishingRecoverer as the original raw bytes,
     *    bypassing this value serializer entirely.
     */
    @Bean
    public KafkaTemplate<String, Object> deadLetterKafkaTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, Object> factory =
                new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(factory);
    }

    /**
     * Error handler: retry a fixed number of times, then route to "<original-topic>.DLT".
     * The DLT record keeps the original partition number.
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, Object> deadLetterKafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        deadLetterKafkaTemplate,
                        (record, exception) -> {
                            log.error(
                                    "Sending record to dead-letter topic. topic={}, partition={}, offset={}, key={}",
                                    record.topic(),
                                    record.partition(),
                                    record.offset(),
                                    record.key(),
                                    exception);
                            return new TopicPartition(
                                    record.topic() + ".DLT",
                                    record.partition());
                        });

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(RETRY_INTERVAL_MS, RETRY_ATTEMPTS));

        // These are permanent errors: retrying will never help, so skip
        // straight to the dead-letter topic on the first failure.
        errorHandler.addNotRetryableExceptions(
                DeserializationException.class,
                IllegalArgumentException.class);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn(
                        "Retry attempt {} for record. topic={}, offset={}, cause={}",
                        deliveryAttempt,
                        record.topic(),
                        record.offset(),
                        ex.getMessage()));

        return errorHandler;
    }

    /**
     * Consumer factory built from the same spring.kafka.consumer.* properties
     * defined in application.yml (deserializers, group-id, trusted packages...).
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props =
                kafkaProperties.buildConsumerProperties(null);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Listener container factory wired with the error handler and record-level
     * acknowledgement, so a record is only committed after it is processed
     * successfully (or recovered to the DLT).
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler kafkaErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setAutoStartup(autoStartup);
        return factory;
    }
}
