package com.cinemesh.paymentservice.presentation.rest;

import com.cinemesh.paymentservice.application.dto.request.PaymentRequestPayload;
import com.cinemesh.paymentservice.application.dto.response.PaymentRequestResponse;
import com.cinemesh.paymentservice.application.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;

    @PostMapping("/payment-requests")
    public PaymentRequestResponse createPaymentRequest(
            @RequestBody @Valid PaymentRequestPayload payload, HttpServletRequest request) {

        // Extract client IP (handles reverse proxies like Nginx/Load Balancers)
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        return paymentService.createPaymentRequest(payload, ipAddress);
    }

}
