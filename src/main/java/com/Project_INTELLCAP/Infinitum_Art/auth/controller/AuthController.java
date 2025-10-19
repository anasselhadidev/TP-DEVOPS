package com.Project_INTELLCAP.Infinitum_Art.auth.controller;

import com.Project_INTELLCAP.Infinitum_Art.auth.DTO.*;
import com.Project_INTELLCAP.Infinitum_Art.DTOsGlobal.MessageResponse;
import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.auth.services.AuthService;
import com.Project_INTELLCAP.Infinitum_Art.auth.utils.CookieUtil;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        // Input validation - let global handler catch IllegalArgumentException
        if (authRequest.email() == null || authRequest.password() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        AuthResponse authResponse = authService.login(authRequest);

        // Set cookie in response header
        ResponseCookie jwtCookie = CookieUtil.createJwtCookie(authResponse.getToken(), "Authorization", 3600);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // Return user info without token
        return ResponseEntity.ok(authResponse.getUser());
    }

    @PermitAll
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserCreateRequest request) {
        // Business logic validation
        if(request.role() != UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role");
        }

        authService.createUser(request);
        return ResponseEntity.ok(new MessageResponse("User created successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Create expired cookie
        ResponseCookie expiredCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(
            @Valid @RequestBody PasswordResetInitRequest request) {
        authService.initiatePasswordReset(request.email());
        return ResponseEntity.ok(new MessageResponse("Password reset link sent to your email"));
    }




    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }
}