package com.Project_INTELLCAP.Infinitum_Art.user.service;

import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Token;
import com.Project_INTELLCAP.Infinitum_Art.auth.repo.TokenRepo;
import com.Project_INTELLCAP.Infinitum_Art.auth.services.EmailService;
import com.Project_INTELLCAP.Infinitum_Art.user.DTO.PasswordChangeRequest;
import com.Project_INTELLCAP.Infinitum_Art.user.DTO.UserProfileUpdateRequest;
import com.Project_INTELLCAP.Infinitum_Art.user.DTO.UserStatusUpdateRequest;
import com.Project_INTELLCAP.Infinitum_Art.user.DTO.UserUpdateRequest;
import com.Project_INTELLCAP.Infinitum_Art.auth.services.FileStorageService;
import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final FileStorageService fileStorageService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepo tokenRepo;
    private final EmailService emailService;




    public void changePassword(Users user, PasswordChangeRequest request) {
        // 1. Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        // 2. Validate new password complexity
        if (request.newPassword().equals(request.currentPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // 3. Confirm password match
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New passwords don't match");
        }

        // 4. Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);
    }


    @Transactional
    public Users updateUserStatus(Long userId, UserStatusUpdateRequest request, Users adminUser) {
        // Vérifier que l'admin existe et a les droits
        if (adminUser == null || adminUser.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can update user status");
        }

        // Récupérer l'utilisateur à mettre à jour
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Empêcher la désactivation d'un compte admin
        if (user.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify admin account status");
        }

        // Empêcher l'auto-désactivation
        if (user.getId().equals(adminUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify your own account status");
        }

        // Mettre à jour le statut
        user.setActive(request.getActive());

        // Si le compte est désactivé, on le verrouille aussi
        if (!request.getActive()) {
            user.setLocked(true);
        } else {
            user.setLocked(false);
        }

        return userRepo.save(user);
    }

    public Users updateUserProfile(Users user, UserProfileUpdateRequest updateRequest) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        // Vérifier si l'email doit être changé
        if (updateRequest.getEmail() != null) {
            if (!updateRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (!updateRequest.getEmail().equals(user.getEmail())) {

                // Vérifier si l'email est déjà utilisé
                if (userRepo.existsByEmail(updateRequest.getEmail())) {
                    throw new DataIntegrityViolationException("Email already in use");
                }

                // Mettre à jour les autres champs sauf l'email (comme dans le register)
                updateOtherFields(user, updateRequest);
                userRepo.save(user);

                // Supprimer les anciens tokens de vérification pour cet utilisateur
                System.out.println("Deleting old tokens for user: " + user.getId());
                tokenRepo.deleteByUser(user);

                // Créer un nouveau token avec le nouvel email (comme dans le register)
                Token verificationToken = new Token(user);
                verificationToken.setNewEmail(updateRequest.getEmail());
                tokenRepo.save(verificationToken);


                // Vérifier que le token est bien en base de données
                Token savedToken = tokenRepo.findByToken(verificationToken.getToken());

                emailService.sendEmailChangeVerification(user, verificationToken.getToken(), updateRequest.getEmail(), "Verify your new email address");

                // Retourner 202 Accepted avec message de vérification
                throw new ResponseStatusException(HttpStatus.ACCEPTED,
                        "A verification email has been sent to " + updateRequest.getEmail() + ". Please check your inbox to confirm the email change.");
            }
        }

        // Si pas de changement d'email, mettre à jour les autres champs
        updateOtherFields(user, updateRequest);
        return userRepo.save(user);
    }

    private void updateOtherFields(Users user, UserProfileUpdateRequest updateRequest) {
        // Mise à jour du nom
        if (updateRequest.getFullName() != null) {
            if (updateRequest.getFullName().trim().isEmpty()) {
                throw new IllegalArgumentException("Full name cannot be empty");
            }
            user.setUsername(updateRequest.getFullName());
        }

        // Mise à jour du numéro de téléphone
        if (updateRequest.getPhoneNumber() != null) {
            if (!updateRequest.getPhoneNumber().matches("^\\+?[1-9][0-9]{7,14}$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid phone number format. Must be 8-15 digits and may start with +");
            }
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        // Mise à jour de l'image
        try {
            if (updateRequest.getImage() != null && !updateRequest.getImage().isEmpty()) {
                if (user.getImageUrl() != null) {
                    fileStorageService.deleteFile(user.getImageUrl());
                }
                String imageUrl = fileStorageService.storeProfileImage(updateRequest.getImage(), user.getId());
                user.setImageUrl(imageUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }
    }
}
