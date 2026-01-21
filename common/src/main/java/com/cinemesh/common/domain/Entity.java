package com.cinemesh.common.domain;

import com.cinemesh.common.event.DomainEvent;

import java.util.List;

public interface Entity<TId> {

    TId getId();

    List<DomainEvent> getEvents();

    void addEvent(DomainEvent event);

}
