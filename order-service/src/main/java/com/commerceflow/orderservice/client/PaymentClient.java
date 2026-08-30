package com.commerceflow.orderservice.client;

import com.commerceflow.orderservice.dto.request.CreatePaymentRequest;
import com.commerceflow.orderservice.dto.response.PaymentResponse;
import com.commerceflow.orderservice.exception.PaymentFailedException;
import com.commerceflow.orderservice.exception.PaymentServiceUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder
                .baseUrl("http://payment-service")
                .build();
        this.objectMapper = objectMapper;
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentServiceUnavailable")
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        try {
            return restClient
                    .post()
                    .uri("/api/v1/payments")
                    .body(request)
                    .retrieve()
                    .body(PaymentResponse.class);

        } catch (HttpClientErrorException ex) {
            throw new PaymentFailedException(extractMessage(ex, "Payment could not be created."));

        } catch (HttpServerErrorException | ResourceAccessException ex) {
            throw new PaymentServiceUnavailableException();
        }
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentServiceUnavailable")
    public PaymentResponse processPayment(Long orderId) {
        try {
            return restClient
                    .post()
                    .uri("/api/v1/payments/{orderId}/process", orderId)
                    .retrieve()
                    .body(PaymentResponse.class);

        } catch (HttpClientErrorException ex) {
            throw new PaymentFailedException(extractMessage(ex, "Payment could not be processed."));

        } catch (HttpServerErrorException | ResourceAccessException ex) {
            throw new PaymentServiceUnavailableException();
        }
    }

    private String extractMessage(HttpClientErrorException ex, String fallback) {
        try {
            JsonNode root = objectMapper.readTree(ex.getResponseBodyAsString());
            return root.path("message").asText(fallback);
        } catch (Exception parseEx) {
            return fallback;
        }
    }

    private PaymentResponse paymentServiceUnavailable(CreatePaymentRequest request, Exception exception) {
        return rethrowOrUnavailable(exception);
    }

    private PaymentResponse paymentServiceUnavailable(Long orderId, Exception exception) {
        return rethrowOrUnavailable(exception);
    }

    private PaymentResponse rethrowOrUnavailable(Exception exception) {
        if (exception instanceof PaymentFailedException) {
            throw (PaymentFailedException) exception;
        }
        throw new PaymentServiceUnavailableException();
    }
}
