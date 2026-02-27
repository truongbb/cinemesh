package com.cinemesh.bookingservice.domain.model;

import com.cinemesh.bookingservice.application.dto.OrderDto;
import com.cinemesh.bookingservice.application.dto.TicketDto;
import com.cinemesh.bookingservice.domain.exception.BookingErrorCode;
import com.cinemesh.bookingservice.statics.OrderPaymentStatus;
import com.cinemesh.bookingservice.statics.OrderStatus;
import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.utils.ObjectUtils;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Order extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    private UUID userId;
    private BigDecimal totalAmount;
    private OrderPaymentStatus paymentStatus;
    private OrderStatus status;
    private List<Ticket> tickets;

    public Order() {
        this.id = UUID.randomUUID();
        this.tickets = new ArrayList<>();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.ORDER_CREATED, id));
    }

    public Order(OrderDto orderDto) {
        this.id = orderDto.getId() == null ? UUID.randomUUID() : orderDto.getId();
        this.userId = orderDto.getUserId();
        this.totalAmount = orderDto.getTotalAmount();
        this.paymentStatus = orderDto.getPaymentStatus();
        this.status = orderDto.getStatus();
        this.tickets = orderDto.getTickets()
                .stream()
                .map(ticketDto -> {
                    Ticket ticket = new Ticket(this, ticketDto);
                    addEvent(new CinemeshEvent(CinemeshEventName.TICKET_ADDED, ticket.getId()));
                    return ticket;
                })
                .toList();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.ORDER_CREATED, id));
    }


    public void setUserId(UUID userId) {
        if (ObjectUtils.equals(this.userId, userId)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("userId", this.userId, userId)));
        this.userId = userId;
        modify();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        if (ObjectUtils.equals(this.totalAmount, totalAmount)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("totalAmount", this.totalAmount, totalAmount)));
        this.totalAmount = totalAmount;
        modify();
    }

    public void setPaymentStatus(OrderPaymentStatus paymentStatus) {
        if (ObjectUtils.equals(this.paymentStatus, paymentStatus)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("paymentStatus", this.paymentStatus, paymentStatus)));
        this.paymentStatus = paymentStatus;
        modify();
    }

    public void setStatus(OrderStatus status) {
        if (ObjectUtils.equals(this.status, status)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("status", this.status, status)));
        this.status = status;
        modify();
    }

    /**
     * Chỉ update thông tin Ticket vào order thông qua TicketDto
     * TicketDto không có id: tạo mới item trong order
     * TicketDto có id:
     * case 1: id không tồn tại trong database: throw not found exception
     * case 2: id tồn tại trong database: update bản ghi bình thường
     */
    public void updateTickets(List<TicketDto> ticketDtos) {
        this.tickets = this.tickets == null ? new ArrayList<>() : this.tickets;

        // danh sách phần tử được thêm mới: không truyền lên id
        List<TicketDto> addingTickets = ticketDtos.stream().filter(x -> x.getId() == null).toList();

        // danh sách phần tử truyền lên id
        List<TicketDto> existedTickets = ticketDtos.stream().filter(x -> x.getId() != null).toList();

        // danh sách phần tử được cập nhật: truyền lên id va id ton tai trong db
        List<TicketDto> updatingTickets = existedTickets.stream()
                .filter(dto -> this.tickets.stream().anyMatch(ticket -> ticket.getId().toString().equals(dto.getId().toString())
                )).toList();

        // nếu dto có id mà id ko trong domain thì throw exception
        List<TicketDto> notExistedTickets = existedTickets.stream()
                .filter(dto -> this.tickets.stream().noneMatch(ticket -> ticket.getId().equals(dto.getId()))).toList();

        if (!notExistedTickets.isEmpty()) {
            throw new NotFoundException(BookingErrorCode.TICKET_NOT_FOUND);
        }

        // list sẽ delete khỏi list cũ
        List<Ticket> deletingTickets = this.tickets.stream()
                .filter(ticket -> existedTickets.stream().noneMatch(ticketDto -> ticketDto.getId().equals(ticket.getId())))
                .toList();

        addTicket(addingTickets);
        removeTicket(deletingTickets);
        updateTicket(updatingTickets);

    }


    public void addTicket(List<TicketDto> dtos) {
        dtos.forEach(it -> {
            Ticket seat = new Ticket(this, it);
            this.tickets.add(seat);
            addEvent(new CinemeshEvent(CinemeshEventName.TICKET_ADDED, seat.getId()));
            modify();
        });
    }

    private void removeTicket(List<Ticket> seats) {
        seats.forEach(it -> {
            this.tickets.removeIf(obj -> obj.getId().equals(it.getId()));
            addEvent(new CinemeshEvent(CinemeshEventName.TICKET_REMOVED, it.getId()));
            modify();
        });
    }

    private void updateTicket(List<TicketDto> dtos) {
        for (TicketDto dto : dtos) {
            this.tickets.stream()
                    .filter(x -> x.getId().equals(dto.getId()))
                    .findFirst()
                    .ifPresent(seat -> {
                        seat.update(dto);
                        if (seat.isModified()) {
                            addEvent(new CinemeshEvent(CinemeshEventName.TICKET_UPDATED, seat.getId()));
                            modify();
                        }
                    });
        }
    }

}
