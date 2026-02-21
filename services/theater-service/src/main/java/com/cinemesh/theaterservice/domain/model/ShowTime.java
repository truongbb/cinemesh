package com.cinemesh.theaterservice.domain.model;

import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.utils.ObjectUtils;
import com.cinemesh.theaterservice.application.dto.ShowtimeDto;
import com.cinemesh.theaterservice.statics.ShowtimeStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ShowTime extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    /**
     * Thiết kế chỉ lưu id của movie và room là vì:
     * - domain class Movie nằm bên movie-service
     * - mặc dù domain class Room nằm cùng service với domain ShowTime, nhưng chính ra thì show time nên là 1 service riêng,
     * ............. ở đây chỉ cắt bớt và rút ngắn thiết kế mà thôi
     * - việc lưu các id chứ không phải domain class này nhằm mục đích dễ mở rộng,
     * ............. sau này có phai tách show time ra 1 service riêng thì việc lưu id này không ảnh hưởng gì.
     * ............. Nếu như lưu object thì sau này tách service class domain của các object đó sẽ không đi cùng, gây khó khăn khi mở rộng.
     */

    private UUID movieId;
    private UUID roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;
    private ShowtimeStatus status;

    public ShowTime() {
        this.id = UUID.randomUUID();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.SHOWTIME_CREATED, id));
    }

    public ShowTime(ShowtimeDto dto) {
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        this.movieId = dto.getMovieId();
        this.roomId = dto.getRoomId();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
        this.basePrice = dto.getBasePrice();
        this.startTime = dto.getStartTime();
        addEvent(new CinemeshEvent(CinemeshEventName.SHOWTIME_CREATED, id));
        create();
    }

    public void update(ShowtimeDto dto) {
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        setMovieId(dto.getMovieId());
        setRoomId(dto.getRoomId());
        setStartTime(dto.getStartTime());
        setEndTime(dto.getEndTime());
        setBasePrice(dto.getBasePrice());
        setStatus(dto.getStatus());
        modify();
    }

    public void setMovieId(UUID movieId) {
        if (ObjectUtils.equals(this.movieId, movieId)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("movieId", this.movieId, movieId)));
        this.movieId = movieId;
        modify();
    }

    public void setRoomId(UUID roomId) {
        if (ObjectUtils.equals(this.roomId, roomId)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("roomId", this.roomId, roomId)));
        this.roomId = roomId;
        modify();
    }

    public void setStartTime(LocalDateTime startTime) {
        if (ObjectUtils.equals(this.startTime, startTime)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("startTime", this.startTime, startTime)));
        this.startTime = startTime;
        modify();
    }

    public void setEndTime(LocalDateTime endTime) {
        if (ObjectUtils.equals(this.endTime, endTime)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("endTime", this.endTime, endTime)));
        this.endTime = endTime;
        modify();
    }

    public void setBasePrice(BigDecimal basePrice) {
        if (ObjectUtils.equals(this.endTime, basePrice)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("basePrice", this.basePrice, basePrice)));
        this.basePrice = basePrice;
        modify();
    }

    public void setStatus(ShowtimeStatus status) {
        if (ObjectUtils.equals(this.status, status)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("status", this.status, status)));
        this.status = status;
        modify();
    }

}
