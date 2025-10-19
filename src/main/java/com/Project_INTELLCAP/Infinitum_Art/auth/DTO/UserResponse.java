package com.Project_INTELLCAP.Infinitum_Art.auth.DTO;

import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        String phoneNumber,
        UserRole role,
        LocalDateTime creationDate,
        String imageUrl,
        LocalDateTime lastLogin,
        boolean isEmailVerified,
        boolean locked,
        boolean expired,
        boolean credentialsExpired
) {
    public static UserResponse fromEntity(Users user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreationDate(),
                user.getImageUrl(),
                user.getLastLogin(),
                user.isEmailVerified(),
                user.isLocked(),
                user.isExpired(),
                user.isCredentialsExpired()
        );
    }
}