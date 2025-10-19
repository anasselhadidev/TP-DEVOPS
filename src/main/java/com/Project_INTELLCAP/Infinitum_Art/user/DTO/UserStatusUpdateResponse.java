package com.Project_INTELLCAP.Infinitum_Art.user.DTO;

import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;

public record UserStatusUpdateResponse(
        Long id,
        String email,
        String username,
        UserRole role,
        boolean active,
        String message
) {
    public static UserStatusUpdateResponse fromEntity(com.Project_INTELLCAP.Infinitum_Art.user.modele.Users user, String message) {
        return new UserStatusUpdateResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.isActive(),
                message
        );
    }
}