package com.cinemesh.bookingservice.infrastructure.persistence.entity;

import com.cinemesh.bookingservice.statics.OrderPaymentStatus;
import com.cinemesh.bookingservice.statics.OrderStatus;
import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity {

    private UUID userId;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderPaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonManagedReference
    private List<TicketEntity> tickets;

    @Version
    private Integer version;

}
