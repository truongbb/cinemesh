package com.cinemesh.bookingservice.infrastructure.persistence.entity;

import com.cinemesh.bookingservice.statics.TicketStatus;
import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity extends BaseEntity {

    private UUID showtimeId;

    private UUID seatId;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    OrderEntity order;

}
