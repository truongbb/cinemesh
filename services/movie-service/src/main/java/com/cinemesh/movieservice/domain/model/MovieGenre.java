package com.cinemesh.movieservice.domain.model;

import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.movieservice.application.dto.MovieGenreDto;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.utils.ObjectUtils;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MovieGenre extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    private String name;

    public MovieGenre() {
        this.id = UUID.randomUUID();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.GENRE_CREATED, id));
    }

    public MovieGenre(MovieGenreDto dto) {
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        this.name = dto.getName();
        create();
    }

    public void setName(String name) {
        if (ObjectUtils.equals(this.name, name)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("name", this.name, name)));
        this.name = name;
        modify();
    }

}
