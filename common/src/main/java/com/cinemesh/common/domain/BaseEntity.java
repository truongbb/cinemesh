package com.cinemesh.common.domain;

import com.cinemesh.common.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEntity<TId> implements Entity<TId> {
    protected TId id;
    private List<DomainEvent> events = new ArrayList();
    private Integer version;
    private Long createdBy;
    private Date createdDate;
    private Date modifiedDate;
    private boolean created;
    private boolean modified;

    protected void create() {
        this.created = true;
        this.createdBy = -1l;
        this.createdDate = new Date();
        this.modifiedDate = new Date();
    }

    protected void modify() {
        this.modified = true;
        this.modifiedDate = new Date();
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

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public boolean isCreated() {
        return this.created;
    }

    public boolean isModified() {
        return this.modified;
    }
}
