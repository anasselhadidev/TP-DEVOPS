package com.Project_INTELLCAP.Infinitum_Art.user.DTO;

import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

public record UserUpdateResponse(
        Long id,
        String email,
        String fullName,
        UserRole role,
        LocalDateTime creationDate,
        String imageUrl
) {
    // Conversion method in your Users entity
    public static UserUpdateResponse fromEntity(Users user) {
        String relativePath = user.getImageUrl();
        String fullImageUrl = null;

        if (relativePath != null && !relativePath.isEmpty()) {
            fullImageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path(relativePath)
                    .toUriString();
        }

        return new UserUpdateResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getCreationDate(),
                fullImageUrl);
    }
}