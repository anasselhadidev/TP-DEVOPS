package com.Project_INTELLCAP.Infinitum_Art.user.DTO;

import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String email,
        String fullName,
        UserRole role,
        LocalDateTime creationDate,
        String imageUrl,
        String phoneNumber
) {}