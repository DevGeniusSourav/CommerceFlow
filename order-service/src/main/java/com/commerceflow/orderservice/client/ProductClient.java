package com.commerceflow.orderservice.client;

import com.commerceflow.orderservice.dto.response.ProductSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ProductClient {
    private final RestClient.Builder restClientBuilder;

    public ProductSummaryResponse getProduct(Long productId){
        RestClient restClient = restClientBuilder
                .baseUrl("http://product-service")
                .build();

        return restClient
                .get()
                .uri("/api/v1/internal/products/{id}", productId)
                .retrieve()
                .body(ProductSummaryResponse.class);
    }
}
