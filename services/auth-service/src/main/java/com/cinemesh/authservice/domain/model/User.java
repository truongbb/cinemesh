package com.cinemesh.authservice.domain.model;

import com.cinemesh.authservice.statics.UserStatus;
import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.utils.ObjectUtils;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class User extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    private String email;
    private String password;
    private String fullName;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String avatarUrl;
    private UserStatus status;
    private Set<Role> roles;

    public User() {
        UUID id = UUID.randomUUID();
        this.id = id;
        this.roles = new HashSet<>();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.USER_CREATED, id));
    }

    public void setEmail(String email) {
        if (ObjectUtils.equals(this.email, email)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("email", this.email, email)));
        this.email = email;
        modify();
    }

    public void setPassword(String password) {
        if (ObjectUtils.equals(this.password, password)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("password", this.password, password)));
        this.password = password;
        modify();
    }

    public void setFullName(String fullName) {
        if (ObjectUtils.equals(this.fullName, fullName)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("fullName", this.fullName, fullName)));
        this.fullName = fullName;
        modify();
    }

    public void setPhone(String phone) {
        if (ObjectUtils.equals(this.phone, phone)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("phone", this.phone, phone)));
        this.phone = phone;
        modify();
    }

    public void setDob(LocalDate dob) {
        if (ObjectUtils.equals(this.dob, dob)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("dob", this.dob, dob)));
        this.dob = dob;
        modify();
    }

    public void setGender(String gender) {
        if (ObjectUtils.equals(this.gender, gender)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("gender", this.gender, gender)));
        this.gender = gender;
        modify();
    }

    public void setAvatarUrl(String avatarUrl) {
        if (ObjectUtils.equals(this.avatarUrl, avatarUrl)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("avatarUrl", this.avatarUrl, avatarUrl)));
        this.avatarUrl = avatarUrl;
        modify();
    }

    public void setStatus(UserStatus status) {
        if (ObjectUtils.equals(this.status, status)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("status", this.status, status)));
        this.status = status;
        modify();
    }

    public void addRoles(List<RoleDto> roleDtos) {
        if (CollectionUtils.isEmpty(roleDtos)) this.roles = new HashSet<>();
        roleDtos.forEach(roleDto -> {
            Role role = new Role(roleDto);
            this.roles.add(role);
            addEvent(new CinemeshEvent(CinemeshEventName.USER_ROLES_ADDED, roleDto.getId().toString()));
        });
        modify();
    }

    public void removeRoles(List<RoleDto> roleDtos) {
        roleDtos.forEach(it -> {
            this.roles.removeIf(obj -> obj.getId().equals(it.getId()));
            addEvent(new CinemeshEvent(CinemeshEventName.USER_ROLES_REMOVED, it.getId().toString()));
            modify();
        });
    }

}
