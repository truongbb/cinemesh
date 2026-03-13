//package com.cinemesh.cinemeshgateway.filter;
//
//
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@Component
//// HIGHEST_PRECEDENCE guarantees this is the very first thing Spring does
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class CorrelationIdWebFilter implements WebFilter {
//
//    private static final String REQUEST_ID_HEADER = "X-Request-ID";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        // 1. Generate the ID natively
//        String correlationId = UUID.randomUUID().toString().replace("-", "");
//
//        // 2. Inject into the Request (so downstream services like booking-service can read it)
//        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                .header(REQUEST_ID_HEADER, correlationId)
//                .build();
//        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
//
//        // 4. Continue the server exchange
//        return chain.filter(mutatedExchange);
//    }
//}