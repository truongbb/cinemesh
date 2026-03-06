
package com.cinemesh.common.dto.domain;

import com.cinemesh.common.statics.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDomainDto extends BaseDomainEntityDto<UUID> {

    private UUID showtimeId;
    private UUID seatId;
    private BigDecimal price;
    private TicketStatus status;
}
