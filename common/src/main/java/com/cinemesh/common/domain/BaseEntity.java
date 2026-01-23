package com.cinemesh.common.domain;

import com.cinemesh.common.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEntity<TId> implements Entity<TId> {
    protected TId id;
    private List<DomainEvent> events;
    private Integer version;
    private String createdBy;
    private Instant createdAt;
    private Instant modifiedAt;
    private boolean created;
    private boolean modified;

    protected void create() {
        this.created = true;
        this.createdBy = SecurityContextHolder.getContext().getAuthentication().getName();
        this.createdAt = Instant.now();
        this.modifiedAt = Instant.now();
    }

    protected void modify() {
        this.modified = true;
        this.modifiedAt = Instant.now();
    }

    public void addEvent(DomainEvent event) {
        this.events = (List<DomainEvent>) (this.events == null ? new ArrayList() : this.events);
        this.events.add(event);
    }

    public TId getId() {
        return this.id;
    }

    public List<DomainEvent> getEvents() {
        return this.events;
    }

    public Integer getVersion() {
        return this.version;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getModifiedAt() {
        return this.modifiedAt;
    }

    public boolean isCreated() {
        return this.created;
    }

    public boolean isModified() {
        return this.modified;
    }
}
