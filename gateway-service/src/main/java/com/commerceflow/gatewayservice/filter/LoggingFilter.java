package com.commerceflow.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID =
            "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {



        long start = System.nanoTime();

        var request = exchange.getRequest();

        String correlationId = request.getHeaders().getFirst(CORRELATION_ID);
        if(correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

//        String finalCorrelationId = correlationId;

        ServerHttpRequest mutatedRequest = request
                .mutate()
                .header(CORRELATION_ID, correlationId).
                build();

        ServerWebExchange mutatedExchange = exchange
                .mutate()
                .request(mutatedRequest)
                .build();

        String method = request.getMethod().name();
        String path = request.getURI().getPath();

        log.info("Incoming Request: {} {}", method, path);

        return chain.filter(mutatedExchange)
                .then(Mono.fromRunnable(() -> {
                    long duration =
                            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                    var response = mutatedExchange.getResponse();
                    var status = response.getStatusCode();
                    log.info("Completed Request: {} {} -> {} ({} ms)", method, path, status != null ? status.value() : "UNKNOWN", duration);
                }));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}