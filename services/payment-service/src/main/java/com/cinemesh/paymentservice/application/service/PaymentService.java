package com.cinemesh.paymentservice.application.service;

import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.OrderStatus;
import com.cinemesh.common.statics.PaymentCurrency;
import com.cinemesh.common.statics.PaymentPartner;
import com.cinemesh.paymentservice.application.dto.PaymentDto;
import com.cinemesh.paymentservice.application.dto.PaymentNotificationDto;
import com.cinemesh.paymentservice.application.dto.request.PaymentRequestPayload;
import com.cinemesh.paymentservice.application.dto.response.PaymentRequestResponse;
import com.cinemesh.paymentservice.domain.exception.PaymentErrorCode;
import com.cinemesh.paymentservice.domain.model.Payment;
import com.cinemesh.paymentservice.domain.model.PaymentNotification;
import com.cinemesh.paymentservice.infrastructure.feign.BookingFeignClient;
import com.cinemesh.paymentservice.infrastructure.feign.response.OrderResponse;
import com.cinemesh.paymentservice.infrastructure.persistence.adapter.PaymentNotificationPersistenceAdapter;
import com.cinemesh.paymentservice.infrastructure.persistence.adapter.PaymentPersistenceAdapter;
import com.cinemesh.paymentservice.statics.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    ObjectMapper objectMapper;
    VNPayUrlGenerator vnPayUrlGenerator;
    BookingFeignClient bookingFeignClient;
    PaymentPersistenceAdapter paymentPersistenceAdapter;
    PaymentNotificationPersistenceAdapter paymentNotificationPersistenceAdapter;

    public PaymentRequestResponse createPaymentRequest(@Valid PaymentRequestPayload request, String ipAddress) {
        // 1. Validate Order exists and is PENDING
        OrderResponse order = bookingFeignClient.getOrderDetails(request.getOrderId());

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new UnprocessableEntityException(PaymentErrorCode.INVALID_ORDER_STATUS_FOR_PAYMENT);
        }

        // 2. Create the Payment Request in the database (Status: PENDING)
        PaymentDto paymentDto = PaymentDto.builder()
                .amount(order.getTotalAmount())
                .paymentPartner(request.getPaymentPartner())
                .orderId(order.getId())
                .currency(PaymentCurrency.VND)
                .status(com.cinemesh.common.statics.PaymentStatus.PENDING)
                .build();
        Payment payment = new Payment(paymentDto);

        paymentPersistenceAdapter.savePayment(payment);

        // 3. Generate the secure VNPay URL (passing the newly created Payment ID)
        String paymentUrl = vnPayUrlGenerator.generatePaymentUrl(
                payment.getId(),
                payment.getAmount(),
                ipAddress,
                request.getPaymentMethod()
        );

        // 4. Return to frontend
        return PaymentRequestResponse.builder()
                .paymentId(payment.getId())
                .paymentUrl(paymentUrl)
                .status(payment.getStatus())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentNotificationError processVNPayWebhook(Map<String, String> vnPayParams) throws JsonProcessingException {
        PaymentNotificationDto notificationDto = PaymentNotificationDto.builder()
                .rawPayload(objectMapper.writeValueAsString(vnPayParams))
                .paymentPartner(PaymentPartner.VN_PAY)
                .status(PaymentNotificationStatus.UNPROCESSED)
                .build();

        String secureHash = vnPayParams.get("vnp_SecureHash");
        vnPayParams.remove("vnp_SecureHash");
        vnPayParams.remove("vnp_SecureHashType");
        String calculatedHash = vnPayUrlGenerator.hashAllFields(vnPayParams);

        if (!calculatedHash.equals(secureHash)) {
            notificationDto.setStatus(PaymentNotificationStatus.FAILED);
            notificationDto.setErrorLog(PaymentNotificationError.INVALID_CHECKSUM.getMessage());
            savePaymentNotification(notificationDto);
            return PaymentNotificationError.INVALID_CHECKSUM;
        }

        try {
            UUID paymentId = UUID.fromString(vnPayParams.get("vnp_TxnRef"));
            Payment payment = paymentPersistenceAdapter.findById(paymentId).orElse(null);
            if (payment == null) {
                notificationDto.setStatus(PaymentNotificationStatus.FAILED);
                notificationDto.setErrorLog(PaymentNotificationError.ORDER_NOT_FOUND.getMessage());
                savePaymentNotification(notificationDto);
                return PaymentNotificationError.ORDER_NOT_FOUND;
            }
            if (!payment.getStatus().equals(com.cinemesh.common.statics.PaymentStatus.PENDING)) {
                notificationDto.setStatus(PaymentNotificationStatus.FAILED);
                notificationDto.setErrorLog(PaymentNotificationError.ORDER_ALREADY_CONFIRMED.getMessage());
                savePaymentNotification(notificationDto);
                return PaymentNotificationError.ORDER_ALREADY_CONFIRMED;
            }

            long vnpAmount = Long.parseLong(vnPayParams.get("vnp_Amount"));
            long dbAmount = payment.getAmount().longValue() * 100;
            if (vnpAmount != dbAmount) {
                notificationDto.setStatus(PaymentNotificationStatus.FAILED);
                notificationDto.setErrorLog(PaymentNotificationError.INVALID_AMOUNT.getMessage());
                savePaymentNotification(notificationDto);
                return PaymentNotificationError.INVALID_AMOUNT;
            }

            String responseCode = vnPayParams.get("vnp_ResponseCode");
            if (PaymentNotificationError.SUCCESS.getCode().equals(responseCode)) {
                notificationDto.setStatus(PaymentNotificationStatus.PROCESSED);
            } else {
                notificationDto.setStatus(PaymentNotificationStatus.FAILED);
            }
            savePaymentNotification(notificationDto);
            return PaymentNotificationError.SUCCESS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            notificationDto.setStatus(PaymentNotificationStatus.FAILED);
            notificationDto.setErrorLog(PaymentNotificationError.UNKNOWN_ERROR.getMessage());
            savePaymentNotification(notificationDto);
            return PaymentNotificationError.UNKNOWN_ERROR;
        }
    }

    private void savePaymentNotification(PaymentNotificationDto notificationDto) {
        PaymentNotification notification = new PaymentNotification(notificationDto);
        paymentNotificationPersistenceAdapter.save(notification);
    }

}
