package com.commerceflow.orderservice.client;

import com.commerceflow.orderservice.dto.request.ConfirmInventoryRequest;
import com.commerceflow.orderservice.dto.request.ReleaseInventoryRequest;
import com.commerceflow.orderservice.dto.request.ReserveInventoryRequest;
import com.commerceflow.orderservice.dto.response.ReservationResponse;
import com.commerceflow.orderservice.exception.InsufficientInventoryException;
import com.commerceflow.orderservice.exception.InventoryServiceUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public InventoryClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder
                .baseUrl("http://inventory-service")
                .build();
        this.objectMapper = objectMapper;
    }

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "inventoryServiceUnavailable"
    )
    public ReservationResponse reserve(ReserveInventoryRequest request) {
        try {
            return restClient
                    .post()
                    .uri("/api/v1/internal/inventory/reserve")
                    .body(request)
                    .retrieve()
                    .body(ReservationResponse.class);

        } catch (HttpClientErrorException.BadRequest ex) {
            throw parseInsufficientInventory(ex);

        } catch (HttpServerErrorException | ResourceAccessException ex) {
            throw new InventoryServiceUnavailableException();
        }
    }

    public void release(Long orderId) {
        restClient
                .post()
                .uri("/api/v1/internal/inventory/release")
                .body(new ReleaseInventoryRequest(orderId))
                .retrieve()
                .toBodilessEntity();
    }

    public ReservationResponse confirm(Long orderId) {
        return restClient
                .post()
                .uri("/api/v1/internal/inventory/confirm")
                .body(new ConfirmInventoryRequest(orderId))
                .retrieve()
                .body(ReservationResponse.class);
    }

    private InsufficientInventoryException parseInsufficientInventory(HttpClientErrorException.BadRequest ex) {
        try {
            JsonNode root = objectMapper.readTree(ex.getResponseBodyAsString());
            JsonNode errors = root.path("errors");

            if (errors.isMissingNode() || errors.isNull()
                    || errors.path("productId").isMissingNode()
                    || errors.path("requestedQuantity").isMissingNode()) {
                // Downstream didn't send structured data — don't fabricate 0/0.
                // Fall back to whatever human-readable message it did send.
                String fallbackMessage = root.path("message").asText("Insufficient inventory.");
                return new InsufficientInventoryException(fallbackMessage);
            }

            Long productId = errors.path("productId").asLong();
            Integer requestedQuantity = errors.path("requestedQuantity").asInt();

            return new InsufficientInventoryException(productId, requestedQuantity);

        } catch (Exception parseEx) {
            return new InsufficientInventoryException("Insufficient inventory for the requested product.");
        }
    }

    private ReservationResponse inventoryServiceUnavailable(ReserveInventoryRequest request, Exception exception) {
        if (exception instanceof InsufficientInventoryException) {
            throw (InsufficientInventoryException) exception;
        }
        throw new InventoryServiceUnavailableException();
    }
}