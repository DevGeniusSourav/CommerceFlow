package com.commerceflow.orderservice.client;

import com.commerceflow.orderservice.dto.response.ProductSummaryResponse;
import com.commerceflow.orderservice.exception.ProductNotFoundException;
import com.commerceflow.orderservice.exception.ProductServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {
    private final RestClient restClient;

    public ProductClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://product-service")
                .build();
    }

    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "productServiceUnavailable"
    )
    @Retry(name = "productService")
    public ProductSummaryResponse getProduct(Long productId) {

        try {

            return restClient
                    .get()
                    .uri("/api/v1/internal/products/{id}", productId)
                    .retrieve()
                    .body(ProductSummaryResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new ProductNotFoundException(productId);

        } catch (IllegalStateException ex){
            throw new ProductServiceUnavailableException();
        }
    }

    private ProductSummaryResponse productServiceUnavailable(Long productId, Exception exception) {
        throw new ProductServiceUnavailableException();
    }
}
