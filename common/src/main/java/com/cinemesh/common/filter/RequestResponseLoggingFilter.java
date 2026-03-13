package com.cinemesh.common.filter;

import com.cinemesh.common.statics.CommonConstant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Component
// Runs immediately after MdcLogFilter (which is HIGHEST_PRECEDENCE)
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;

            String requestBodyStr = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
            String responseBodyStr = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
            String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();

            // Create a structured JSON Object for the log
            ObjectNode logNode = objectMapper.createObjectNode();
            logNode.put(CommonConstant.MDC_KEY, MDC.get(CommonConstant.MDC_KEY));
            logNode.put("uri", request.getRequestURI() + queryString);
            logNode.put("method", request.getMethod());
            logNode.put("timeTakenMs", timeTaken);
            logNode.put("status", response.getStatus());

            // Safely embed the bodies as actual JSON objects (if they are valid JSON)
            logNode.set("requestBody", parseToJsonNode(requestBodyStr));
            logNode.set("responseBody", parseToJsonNode(responseBodyStr));

            // Print it beautifully to the console
//            log.info("\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logNode));
            log.info("\n{}", objectMapper.writeValueAsString(logNode));

            responseWrapper.copyBodyToResponse();
        }
    }

    private JsonNode parseToJsonNode(String content) {
        try {
            if (content == null || content.isBlank()) {
                return objectMapper.nullNode();
            }
            return objectMapper.readTree(content);
        } catch (Exception e) {
            // If it's not valid JSON (e.g. plain text), return it as a simple text node
            return objectMapper.valueToTree(content);
        }
    }

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            if (contentAsByteArray == null || contentAsByteArray.length == 0) return "";
            return new String(contentAsByteArray, characterEncoding != null ? characterEncoding : "UTF-8");
        } catch (Exception e) {
            return "[Error Parsing Body]";
        }
    }


}
