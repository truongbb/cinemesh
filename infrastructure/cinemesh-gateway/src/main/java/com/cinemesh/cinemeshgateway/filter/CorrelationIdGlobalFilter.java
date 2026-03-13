package com.cinemesh.cinemeshgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    // Using strictly lowercase is the safest standard for HTTP/2 and Netty
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String correlationId = UUID.randomUUID().toString().replace("-", "");

        // Safely force the header into the outbound downstream request
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.set(REQUEST_ID_HEADER, correlationId))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1; // Execute right before the routing engine takes over
    }
}
