package com.Project_INTELLCAP.Infinitum_Art.auth.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PublicEndpointsConfig {
    @Bean
    public List<String> publicEndpoints() {
        return List.of(
                "/",
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/request-password-reset",
                "/api/auth/verify-reset-token/**",
                "/api/auth/reset-password",
                "/api/auth/verify/**",
                "/api/auth/resend-verification",
                "/images/**"
        );
    }
}

