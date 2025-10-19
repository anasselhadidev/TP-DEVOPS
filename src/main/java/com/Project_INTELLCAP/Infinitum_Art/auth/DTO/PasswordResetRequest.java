package com.Project_INTELLCAP.Infinitum_Art.auth.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank(message = "token required")
                String token,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be 8-100 characters long")
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        String confirmPassword
) {
    // Optional: Add validation method to check if new passwords match
    public boolean isNewPasswordMatching() {
        return newPassword.equals(confirmPassword);
    }
}
