package com.Project_INTELLCAP.Infinitum_Art.auth.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class AuthResponse {
    private final UserResponse user;
    private final String token;
}
