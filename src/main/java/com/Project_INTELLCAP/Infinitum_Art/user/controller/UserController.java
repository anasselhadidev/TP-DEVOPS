package com.Project_INTELLCAP.Infinitum_Art.user.controller;

import com.Project_INTELLCAP.Infinitum_Art.auth.annotations.CurrentUser;
import com.Project_INTELLCAP.Infinitum_Art.DTOsGlobal.MessageResponse;
import com.Project_INTELLCAP.Infinitum_Art.user.DTO.*;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;



    @PutMapping("/user/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            @CurrentUser Users currentUser) {

        userService.changePassword(currentUser, request);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(@CurrentUser Users currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        UserProfileResponse response = new UserProfileResponse(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getUsername(),
                currentUser.getRole(),
                currentUser.getCreationDate(),
                currentUser.getImageUrl(),
                currentUser.getPhoneNumber()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
            @CurrentUser Users currentUser,
            @ModelAttribute UserProfileUpdateRequest updateRequest) {
        try {
            Users updatedUser = userService.updateUserProfile(currentUser, updateRequest);
            UserProfileResponse response = new UserProfileResponse(
                    updatedUser.getId(),
                    updatedUser.getEmail(),
                    updatedUser.getUsername(),
                    updatedUser.getRole(),
                    updatedUser.getCreationDate(),
                    updatedUser.getImageUrl(),
                    updatedUser.getPhoneNumber()
            );
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.ACCEPTED) {
                // Email en attente de vérification
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(Map.of(
                                "message", e.getReason(),
                                "status", "PENDING_EMAIL_VERIFICATION"
                        ));
            }
            throw e;
        }
    }


    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatusUpdateResponse> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody @Valid UserStatusUpdateRequest request,
            @CurrentUser Users currentUser) {

        Users updatedUser = userService.updateUserStatus(userId, request, currentUser);

        String message = request.getActive()
                ? "User account has been activated successfully"
                : "User account has been deactivated successfully";

        return ResponseEntity.ok(UserStatusUpdateResponse.fromEntity(updatedUser, message));
    }
}