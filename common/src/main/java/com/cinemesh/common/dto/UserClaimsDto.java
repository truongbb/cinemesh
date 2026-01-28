package com.cinemesh.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserClaimsDto {
    private String userId;
    private String email; // Đóng vai trò là Subject
    private String role;

    // Sau này thích thêm gì thì thêm vào đây, không ảnh hưởng hàm generate
    // private String fullName;
    // private String avatarUrl;
}
