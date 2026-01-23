package com.cinemesh.authservice.domain.model;

import com.cinemesh.authservice.domain.exception.AuthErrorCode;
import com.cinemesh.authservice.statics.UserStatus;
import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.utils.ObjectUtils;
import lombok.Getter;

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

    /**
     * Chỉ update thông tin Role vào user thông qua Role
     * RoleDto không có id: tạo mới Item
     * RoleDto có id:
     * case 1: id không tồn tại trong database : throw not found exception
     * case 2: id tồn tại trong database : update bản ghi bình thường
     */
    public void setRoles(List<RoleDto> dtos) {
        this.roles = this.roles == null ? new HashSet<>() : this.roles;

        // danh sách phần tử được thêm mới: không truyền lên id
        List<RoleDto> addingRoles = dtos.stream().filter(x -> x.getId() == null).toList();

        // danh sách phần tử truyền lên id
        List<RoleDto> existedRoles = dtos.stream().filter(x -> x.getId() != null).toList();

        // danh sách phần tử được cập nhật: truyền lên id va id ton tai trong db
        List<RoleDto> updatedRoles = existedRoles.stream()
                .filter(quizDto ->
                        this.roles
                                .stream()
                                .anyMatch(quizRoot -> quizRoot.getId().toString().equals(quizDto.getId().toString())
                                ))
                .toList();

        //nếu dto có id mà id ko trong domain thì throw exception
        List<RoleDto> addDtoIdNotExitDomains = existedRoles.stream()
                .filter(quizDto ->
                        this.roles.stream().noneMatch(quizRoot -> quizRoot.getId().equals(quizDto.getId()))
                )
                .toList();

        if (!addDtoIdNotExitDomains.isEmpty()) {
            throw new NotFoundException(AuthErrorCode.ROLE_NOT_FOUND);
        }

        //list sẽ delete khỏi list cũ
        List<Role> deleteAdds = this.roles.stream()
                .filter(quizRoot -> existedRoles.stream().noneMatch(quizDto -> quizDto.getId().equals(quizRoot.getId())))
                .toList();

        addRoles(addingRoles);
        removeRoles(deleteAdds);
        updateRoles(updatedRoles);
    }

    private void addRoles(List<RoleDto> roleDtos) {
        roleDtos.forEach(it -> {
            Role role = new Role(this, it);
            this.roles.add(role);
            addEvent(new CinemeshEvent(CinemeshEventName.ROLES_ADDED, it.getId()));
            modify();
        });
    }

    private void removeRoles(List<Role> roles) {
        roles.forEach(it -> {
            this.roles.removeIf(obj -> obj.getId().equals(it.getId()));
            addEvent(new CinemeshEvent(CinemeshEventName.ROLES_REMOVED, it.getId().toString()));
            modify();
        });
    }

    private void updateRoles(List<RoleDto> dtos) {
        for (RoleDto dto : dtos) {
            this.roles.stream()
                    .filter(x -> x.getId().equals(dto.getId()))
                    .findFirst()
                    .ifPresent(role -> {
                        role.update(dto);
                        if (role.isModified()) {
                            addEvent(new CinemeshEvent(CinemeshEventName.ROLES_UPDATED, role.getId()));
                            modify();
                        }
                    });
        }
    }

}
