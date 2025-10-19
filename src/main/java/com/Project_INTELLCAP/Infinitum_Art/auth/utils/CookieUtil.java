package com.Project_INTELLCAP.Infinitum_Art.auth.utils;

import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static ResponseCookie createJwtCookie(String token, String cookieName, long maxAgeSeconds) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true) // Protect from XSS
                .secure(true) // Only over HTTPS in production
                .sameSite("None") // Allow cross-origin (for frontend on another domain)
                .path("/") // Cookie is valid for all endpoints
                .maxAge(maxAgeSeconds) // e.g., 3600 for 1 hour
                .build();
    }
}
