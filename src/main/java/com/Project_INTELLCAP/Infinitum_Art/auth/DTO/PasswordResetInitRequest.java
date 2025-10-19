package com.Project_INTELLCAP.Infinitum_Art.auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetInitRequest(
        @NotBlank
        @Email
        String email
) {}
