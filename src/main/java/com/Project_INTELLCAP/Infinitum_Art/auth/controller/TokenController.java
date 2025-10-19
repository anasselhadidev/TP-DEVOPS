package com.Project_INTELLCAP.Infinitum_Art.auth.controller;

import com.Project_INTELLCAP.Infinitum_Art.DTOsGlobal.MessageResponse;
import com.Project_INTELLCAP.Infinitum_Art.auth.DTO.VerifyResetTokenResponse;
import com.Project_INTELLCAP.Infinitum_Art.auth.services.TokenService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class TokenController {
    @Autowired
    private TokenService tokenService;
    @PermitAll
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String verifyToken) {

        tokenService.verify(verifyToken);
        return ResponseEntity.ok(new MessageResponse("Token has been verified successfully."));

    }

    @PermitAll
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationToken(@RequestParam String email) {
        tokenService.resendVerificationToken(email);
        return ResponseEntity.ok(new MessageResponse("Token has been resend successfully."));

    }

    @GetMapping("/verify-reset-token")
    public ResponseEntity<?> verifyResetToken(@RequestParam String token) {
        String tok =tokenService.validateResetToken(token);
        return ResponseEntity.ok(new VerifyResetTokenResponse(tok));
    }
}
