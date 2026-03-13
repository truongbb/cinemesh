package com.cinemesh.common.config;

import com.cinemesh.common.statics.CommonConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Configuration
@ConditionalOnClass(name = {"feign.codec.Encoder", "feign.codec.Decoder"})
// 🌟 THE MAGIC SHIELD: Only load this bean if Feign is actually on the classpath!
public class FeignClientConfig {

    /**
     * 1. TOKEN RELAY (Request Interceptor)
     * Grabs the Authorization header from the incoming HTTP request
     * and attaches it to the outgoing Feign request.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

                if (authHeader != null) {
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, authHeader);
                }
            }
        };
    }

    /**
     * 2. CUSTOM DECODER (Response -> Object)
     * Forces Feign to use your application's primary ObjectMapper
     */
    @Bean
    public Decoder feignDecoder(ObjectMapper objectMapper) {
        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new SpringDecoder(objectFactory);
    }

    /**
     * 3. CUSTOM ENCODER (Object -> Request Body)
     * Forces Feign to use your application's primary ObjectMapper
     */
    @Bean
    public Encoder feignEncoder(ObjectMapper objectMapper) {
        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new SpringEncoder(objectFactory);
    }

    @Bean
    public RequestInterceptor correlationIdInterceptor() {
        return template -> {
            // Grab the ID from the current thread's MDC
            String requestId = MDC.get(CommonConstant.MDC_KEY);

            if (requestId != null) {
                // Inject it into the outbound Feign request
                template.header(CommonConstant.REQUEST_ID_HEADER, requestId);
            }
        };
    }
}
