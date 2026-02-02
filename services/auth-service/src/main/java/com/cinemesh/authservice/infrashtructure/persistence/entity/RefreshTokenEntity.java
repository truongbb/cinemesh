package com.cinemesh.authservice.infrashtructure.persistence.entity;

import com.cinemesh.authservice.statics.RefreshTokenStatus;
import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, unique = true, columnDefinition = "text")
    private String token;

    @Column
    @Enumerated(EnumType.STRING)
    private RefreshTokenStatus status;

}
