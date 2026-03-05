package com.cinemesh.paymentservice.presentation.rest;

import com.cinemesh.paymentservice.application.dto.request.PaymentRequestPayload;
import com.cinemesh.paymentservice.application.dto.response.PaymentRequestResponse;
import com.cinemesh.paymentservice.application.service.PaymentService;
import com.cinemesh.paymentservice.statics.PaymentNotificationError;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> handleVNPayIPN(HttpServletRequest request) throws JsonProcessingException {
        // 1. Extract all query parameters sent by VNPay into a Map
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        // 2. Pass to the Service layer (Executes our Inbox/Outbox DB design)
        // It returns a standard VNPay Code (e.g., "00" for Success, "97" for Invalid Hash)
        PaymentNotificationError responseCode = paymentService.processVNPayWebhook(fields);

        // 3. Build the exact JSON response VNPay demands
        Map<String, String> response = new HashMap<>();
        response.put("RspCode", responseCode.getCode());
        response.put("Message", responseCode.getMessage());
        return ResponseEntity.ok(response);
    }

}
