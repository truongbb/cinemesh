package com.cinemesh.authservice.domain.model;

import com.cinemesh.authservice.domain.value_object.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String avatarUrl;
    private UserStatus status;
    private Set<Role> roles = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Regex Email đơn giản
//    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
//
//    // Constructor đầy đủ (Dùng khi load từ DB lên)
    public User(UUID id, String email, String password, String fullName, String phone,
                LocalDate dob, String gender, String avatarUrl, UserStatus status,
                Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.roles = roles != null ? roles : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
//
//    // Factory Method: Tạo User mới (Dùng khi User đăng ký)
//    public static User create(String email, String encodedPassword, String fullName, Role customerRole) {
//        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
//            throw new AuthDomainException("Invalid email format");
//        }
//        if (encodedPassword == null || encodedPassword.length() < 6) {
//            throw new AuthDomainException("Password is too weak");
//        }
//
//        User user = new User(
//                UUID.randomUUID(),
//                email,
//                encodedPassword,
//                fullName,
//                null, null, null, null, // Các trường optional để null
//                UserStatus.ACTIVE,      // Mặc định là Active
//                new HashSet<>(),
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//        user.roles.add(customerRole);
//        return user;
//    }
//
//    // Logic nghiệp vụ: Khóa tài khoản
//    public void lock() {
//        if (this.status == UserStatus.LOCKED) return;
//        this.status = UserStatus.LOCKED;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    // Logic nghiệp vụ: Cập nhật thông tin
//    public void updateProfile(String fullName, String phone, String avatarUrl) {
//        this.fullName = fullName;
//        this.phone = phone;
//        this.avatarUrl = avatarUrl;
//        this.updatedAt = LocalDateTime.now();
//    }

}
