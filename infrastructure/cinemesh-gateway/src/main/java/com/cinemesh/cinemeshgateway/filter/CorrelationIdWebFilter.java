package com.cinemesh.cinemeshgateway.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
// HIGHEST_PRECEDENCE guarantees this is the very first thing Spring does
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdWebFilter implements WebFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // 1. Try to get existing Request ID from frontend (if they sent one)
        String correlationId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);

        // 2. If missing, generate a new ID natively
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().replace("-", "");
        }

        // 3. Inject into the Request (so downstream services like booking-service can read it)
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(REQUEST_ID_HEADER, correlationId)
                .build();

        // 4. Inject into the Response (so the Frontend can receive the same ID)
        exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, correlationId);

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        // 5. Continue the server exchange
        return chain.filter(mutatedExchange);
    }
}