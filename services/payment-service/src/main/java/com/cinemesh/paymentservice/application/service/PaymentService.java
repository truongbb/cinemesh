package com.cinemesh.paymentservice.application.service;

import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.OrderStatus;
import com.cinemesh.paymentservice.application.dto.PaymentDto;
import com.cinemesh.paymentservice.application.dto.request.PaymentRequestPayload;
import com.cinemesh.paymentservice.application.dto.response.PaymentRequestResponse;
import com.cinemesh.paymentservice.domain.exception.PaymentErrorCode;
import com.cinemesh.paymentservice.domain.model.Payment;
import com.cinemesh.paymentservice.infrastructure.feign.BookingFeignClient;
import com.cinemesh.paymentservice.infrastructure.feign.response.OrderResponse;
import com.cinemesh.paymentservice.infrastructure.persistence.adapter.PaymentPersistenceAdapter;
import com.cinemesh.paymentservice.statics.PaymentCurrency;
import com.cinemesh.paymentservice.statics.PaymentStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    BookingFeignClient bookingFeignClient;
    PaymentPersistenceAdapter paymentPersistenceAdapter;
    VNPayUrlGenerator vnPayUrlGenerator;

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
                .status(PaymentStatus.PENDING)
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

}
