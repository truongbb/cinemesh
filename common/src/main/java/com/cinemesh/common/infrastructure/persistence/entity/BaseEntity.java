package com.cinemesh.common.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass // Để các entity con kế thừa cột
@EntityListeners(AuditingEntityListener.class) // <--- MAGIC Ở ĐÂY
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @CreatedDate // Spring tự điền ngày tạo
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate // Spring tự điền ngày sửa
    @Column(name = "modified_at")
    private Instant modifiedAt;

    @CreatedBy
    @Column
    private String createdBy;

    @LastModifiedBy
    @Column
    private String modifiedBy;

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
    }

}