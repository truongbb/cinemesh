package com.cinemesh.common.event;

import java.io.Serializable;
import java.time.Instant;

public abstract class DomainEvent implements Serializable {
    private String name;
    private Object payload;
    private Instant createdAt;

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public Object getPayload() {
        return this.payload;
    }

    protected void setPayload(Object payload) {
        this.payload = payload;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    protected void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
