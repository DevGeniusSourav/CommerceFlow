package com.commerceflow.orderservice.config;

import com.commerceflow.orderservice.filter.CorrelationIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final CorrelationIdInterceptor correlationInterceptor;

    @Bean
    @LoadBalanced
    RestClient.Builder restClientBuilder() {
        return RestClient.builder().requestInterceptor(correlationInterceptor);
    }

}