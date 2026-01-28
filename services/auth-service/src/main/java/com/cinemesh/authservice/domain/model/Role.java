package com.cinemesh.authservice.domain.model;

import com.cinemesh.common.domain.BaseAggregateRoot;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.statics.RoleName;
import com.cinemesh.common.utils.ObjectUtils;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Role extends BaseAggregateRoot<UUID> {

    private RoleName name;

    public Role() {
        UUID id = UUID.randomUUID();
        this.id = id;
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.ROLE_CREATED, id));
    }

    public Role(RoleDto roleDto) {
        this.id = roleDto.getId() == null ? UUID.randomUUID() : roleDto.getId();
        this.name = roleDto.getName();
        if (roleDto.getId() == null) {
            create();
            addEvent(new CinemeshEvent(CinemeshEventName.ROLE_CREATED, id));
        }
    }

    public void setName(RoleName name) {
        if (ObjectUtils.equals(this.name, name)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("name", this.name.name(), name.name())));
        this.name = name;
        modify();
    }

}
