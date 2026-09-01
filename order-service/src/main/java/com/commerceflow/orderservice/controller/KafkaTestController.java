package com.commerceflow.orderservice.controller;

import com.commerceflow.orderservice.kafka.KafkaTestProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaTestProducer kafkaTestProducer;

    @PostMapping
    public void send(@RequestBody String message) {
        kafkaTestProducer.send(message);
    }
}
