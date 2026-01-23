package com.cinemesh.authservice.domain.model;

import com.cinemesh.common.domain.BaseLocalEntity;
import com.cinemesh.common.domain.LocalEntity;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.statics.RoleName;
import com.cinemesh.common.utils.ObjectUtils;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Role extends BaseLocalEntity<User, UUID> implements LocalEntity<User, UUID> {

    private RoleName name;

    public Role() {
    }

    public Role(User aggRoot, RoleDto dto) {
        this.aggRoot = aggRoot;
        this.id = UUID.randomUUID();
        this.name = dto.getName();
        create();
    }

    protected void update(RoleDto dto) {
        setName(dto.getName());
    }

    public void setName(RoleName name) {
        if (ObjectUtils.equals(this.name, name)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("name", this.name.name(), name.name())));
        this.name = name;
        modify();
    }

}
