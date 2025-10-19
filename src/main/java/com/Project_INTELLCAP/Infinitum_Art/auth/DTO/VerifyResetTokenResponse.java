package com.Project_INTELLCAP.Infinitum_Art.auth.DTO;

import jakarta.validation.constraints.NotBlank;

public record VerifyResetTokenResponse(
        @NotBlank String token
) {
}
