
package com.cinemesh.common.dto.domain;

import com.cinemesh.common.statics.PaymentCurrency;
import com.cinemesh.common.statics.PaymentPartner;
import com.cinemesh.common.statics.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDomainDto extends BaseDomainEntityDto<UUID> {

    private UUID orderId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private PaymentCurrency currency;
    private PaymentPartner paymentPartner;
    private String transactionId;
    private PaymentStatus status;
}
