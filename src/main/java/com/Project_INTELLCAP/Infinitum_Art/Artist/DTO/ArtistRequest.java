package com.Project_INTELLCAP.Infinitum_Art.Artist.DTO;

import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @Size(min = 8) String password,
        UserRole role
) {}
