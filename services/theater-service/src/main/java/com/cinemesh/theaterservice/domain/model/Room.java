package com.cinemesh.theaterservice.domain.model;

import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.utils.ObjectUtils;
import com.cinemesh.theaterservice.application.dto.RoomDto;
import com.cinemesh.theaterservice.application.dto.SeatDto;
import com.cinemesh.theaterservice.domain.exception.TheaterErrorCode;
import com.cinemesh.theaterservice.statics.RoomStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Room extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    private String name;
    private Integer totalSeats;
    private RoomStatus status;
    private List<Seat> seats;

    public Room() {
        this.id = UUID.randomUUID();
        this.seats = new ArrayList<>();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.ROOM_CREATED, id));
    }

    public Room(RoomDto roomDto) {
        this.id = roomDto.getId() == null ? UUID.randomUUID() : roomDto.getId();
        this.name = roomDto.getName();
        this.totalSeats = roomDto.getTotalSeats();
        this.status = roomDto.getStatus();
        this.seats = roomDto.getSeats()
                .stream()
                .map(seatDto -> {
                    Seat seat = new Seat(this, seatDto);
                    addEvent(new CinemeshEvent(CinemeshEventName.SEAT_ADDED, seat.getId()));
                    return seat;
                })
                .toList();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.ROOM_CREATED, id));
    }

    public void setName(String name) {
        if (ObjectUtils.equals(this.name, name)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("name", this.name, name)));
        this.name = name;
        modify();
    }

    public void setTotalSeats(Integer totalSeats) {
        if (ObjectUtils.equals(this.totalSeats, totalSeats)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("totalSeats", this.totalSeats, totalSeats)));
        this.totalSeats = totalSeats;
        modify();
    }

    public void setStatus(RoomStatus status) {
        if (ObjectUtils.equals(this.status, status)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("status", this.status, status)));
        this.status = status;
        modify();
    }


    /**
     * Chỉ update thông tin Seat vào room thông qua SeatDto
     * SeatDto không có id: tạo mới item trong room
     * SeatDto có id:
     * case 1: id không tồn tại trong database: throw not found exception
     * case 2: id tồn tại trong database: update bản ghi bình thường
     */
    public void updateSeats(List<SeatDto> seatDtos) {
        this.seats = this.seats == null ? new ArrayList<>() : this.seats;

        // danh sách phần tử được thêm mới: không truyền lên id
        List<SeatDto> addingSeats = seatDtos.stream().filter(x -> x.getId() == null).toList();

        // danh sách phần tử truyền lên id
        List<SeatDto> existedSeats = seatDtos.stream().filter(x -> x.getId() != null).toList();

        // danh sách phần tử được cập nhật: truyền lên id va id ton tai trong db
        List<SeatDto> updatingSeats = existedSeats.stream()
                .filter(dto -> this.seats.stream().anyMatch(seat -> seat.getId().toString().equals(dto.getId().toString())
                )).toList();

        // nếu dto có id mà id ko trong domain thì throw exception
        List<SeatDto> notExistedSeats = existedSeats.stream()
                .filter(dto -> this.seats.stream().noneMatch(seat -> seat.getId().equals(dto.getId()))).toList();

        if (!notExistedSeats.isEmpty()) {
            throw new NotFoundException(TheaterErrorCode.SEAT_NOT_FOUND);
        }

        // list sẽ delete khỏi list cũ
        List<Seat> deletingSeats = this.seats.stream()
                .filter(seat -> existedSeats.stream().noneMatch(seatDto -> seatDto.getId().equals(seat.getId())))
                .toList();

        addSeats(addingSeats);
        removeSeats(deletingSeats);
        updateSeat(updatingSeats);
    }

    public void addSeats(List<SeatDto> dtos) {
        dtos.forEach(it -> {
            Seat seat = new Seat(this, it);
            this.seats.add(seat);
            addEvent(new CinemeshEvent(CinemeshEventName.SEAT_ADDED, seat.getId()));
            modify();
        });
    }

    private void removeSeats(List<Seat> seats) {
        seats.forEach(it -> {
            this.seats.removeIf(obj -> obj.getId().equals(it.getId()));
            addEvent(new CinemeshEvent(CinemeshEventName.SEAT_UPDATED, it.getId()));
            modify();
        });
    }

    private void updateSeat(List<SeatDto> dtos) {
        for (SeatDto dto : dtos) {
            this.seats.stream()
                    .filter(x -> x.getId().equals(dto.getId()))
                    .findFirst()
                    .ifPresent(seat -> {
                        seat.update(dto);
                        if (seat.isModified()) {
                            addEvent(new CinemeshEvent(CinemeshEventName.SEAT_REMOVED, seat.getId()));
                            modify();
                        }
                    });
        }
    }

}
