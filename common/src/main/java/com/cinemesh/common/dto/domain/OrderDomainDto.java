
package com.cinemesh.common.dto.domain;

import com.cinemesh.common.statics.OrderStatus;
import com.cinemesh.common.statics.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDomainDto extends BaseDomainEntityDto<UUID> {

    private UUID userId;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
    private List<TicketDomainDto> tickets;
}
