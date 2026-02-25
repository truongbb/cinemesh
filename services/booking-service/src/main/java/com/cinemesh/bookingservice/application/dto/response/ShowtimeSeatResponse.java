package com.cinemesh.bookingservice.application.dto.response;

import com.cinemesh.bookingservice.statics.SeatBookingStatus;
import com.cinemesh.common.statics.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeSeatResponse {

    private List<ShowtimeSeatResponseDetail> seats;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShowtimeSeatResponseDetail {
        private UUID id;
        private String rowCode;
        private Integer columnNumber;
        private SeatType type;
        private BigDecimal price;
        private SeatBookingStatus status;
    }
}
