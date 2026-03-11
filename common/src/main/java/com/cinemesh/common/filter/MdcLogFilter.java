package com.cinemesh.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLogFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Try to get the ID from the Gateway
        String requestId = request.getHeader(REQUEST_ID_HEADER);

        // 2. Fallback just in case someone calls the microservice directly bypassing the Gateway
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }

        try {
            // 3. Put it in MDC (ThreadLocal)
            MDC.put(MDC_KEY, requestId);

            // 4. Continue standard execution
            filterChain.doFilter(request, response);
        } finally {
            // 5. CRITICAL: Clean up the MDC after the request finishes to prevent memory leaks in Tomcat thread pools
            MDC.remove(MDC_KEY);
        }
    }
}
