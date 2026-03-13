package com.cinemesh.common.interceptor;

import com.cinemesh.common.statics.CommonConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@ConditionalOnClass(name = "feign.RequestInterceptor")
// 🌟 THE MAGIC SHIELD: Only load this bean if Feign is actually on the classpath!
public class FeignCorrelationInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 1. First try the MDC (If Feign is on the same thread, this works perfectly)
        String requestId = MDC.get(CommonConstant.MDC_KEY);
        String authHeader = null;

        // 2. If MDC is empty or we need other headers, fetch the original HTTP request!
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest originalRequest = attributes.getRequest();
            
            // Get Auth header
            authHeader = originalRequest.getHeader(HttpHeaders.AUTHORIZATION);

            // If MDC didn't have the request ID, try getting it from the request headers
            if (requestId == null || requestId.isEmpty()) {
                requestId = originalRequest.getHeader(CommonConstant.REQUEST_ID_HEADER);

                // Fallback to the response header if we injected it there
                if (requestId == null) {
                    requestId = attributes.getResponse().getHeader(CommonConstant.REQUEST_ID_HEADER);
                }
            }
        }

        // 3. Inject it into the outbound Feign call
        if (requestId != null && !requestId.isEmpty()) {
            template.header(CommonConstant.REQUEST_ID_HEADER, requestId);
        } else {
            System.out.println("🚨 CRITICAL: Feign Interceptor failed to find the Request ID in MDC or RequestContext!");
        }

        if (authHeader != null && !authHeader.isEmpty()) {
            template.header(HttpHeaders.AUTHORIZATION, authHeader);
        }
    }

}
