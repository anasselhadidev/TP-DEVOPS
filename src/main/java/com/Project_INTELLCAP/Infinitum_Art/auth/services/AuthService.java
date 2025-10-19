package com.Project_INTELLCAP.Infinitum_Art.auth.services;

import com.Project_INTELLCAP.Infinitum_Art.auth.DTO.*;
import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Client;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Token;
import com.Project_INTELLCAP.Infinitum_Art.auth.repo.TokenRepo;
import com.Project_INTELLCAP.Infinitum_Art.client.repo.ClientRepo;
import com.Project_INTELLCAP.Infinitum_Art.user.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepo userRepo;


    private final ClientRepo clientRepo;


    private final TokenRepo tokenRepo;


    private final AuthenticationManager authenticationManager;


    private final JWTService jwtService;


    private final PasswordEncoder passwordEncoder;


    private final FileStorageService fileStorageService;


    private final EmailService emailService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public void createUser(UserCreateRequest request) {
        if (request.role() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot create admin accounts");
        }

        if (userRepo.existsByEmail(request.email())) {
            throw new DataIntegrityViolationException("Email already registered");
        }

        if (request.role() != UserRole.CLIENT) {
            throw new AccessDeniedException("Invalid role for user creation");
        }

        Users newUser = new Users();
        newUser.setUsername(request.fullName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        newUser.setLocked(false);
        newUser.setCreationDate(LocalDateTime.now());

        newUser = userRepo.save(newUser);
        clientRepo.save(new Client(newUser));

        Token token = new Token(newUser);
        tokenRepo.save(token);
        emailService.sendVerificationEmail(newUser, token.getToken());
    }

    public void activateUser(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.isActive()) {
            throw new IllegalStateException("User account is already active");
        }

        user.setActive(true);
        user.setExpired(false);
        user.setLocked(false);
        userRepo.save(user);
    }

    public void setUserVerified(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (!user.isActive()) {
            throw new DisabledException("Account not activated");
        }

        if (user.isLocked()) {
            throw new LockedException("Account locked");
        }

        if (user.isExpired()) {
            throw new AccountExpiredException("Account expired");
        }
        if(user.isEmailVerified()){
            throw new IllegalStateException("User account is already verified");
        }

        user.setEmailVerified(true);
        userRepo.save(user);
    }




    public AuthResponse login(AuthRequest authRequest) {
        Users user = userRepo.findByEmail(authRequest.email());
        System.out.println("user: " + user);
        if(user == null) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new DisabledException("Account not activated");
        }

        if (user.isLocked()) {
            throw new LockedException("Account locked");
        }

        if (user.isExpired()) {
            throw new AccountExpiredException("Account expired");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

        return new AuthResponse(
                UserResponse.fromEntity(user),
                token
        );
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        Users user = userRepo.findByEmail(email);
        if (user == null) {
            throw new BadCredentialsException("email not found");
        }
        try {
            tokenRepo.deleteByUser(user);
            Token token = new Token(user);
            emailService.sendResetEmail(user, token.getToken());
            tokenRepo.save(token);

        } catch (
        MailAuthenticationException e) {
            throw new MailAuthenticationException("Email service authentication failed. Please contact support.");
        } catch (MailSendException e) {
            throw new MailSendException("Unable to send password reset email. Please try again later.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email due to an unexpected error", e);
        }
    }



    public void resetPassword(PasswordResetRequest request) {
        // 1. Validate token
        Token verificationToken = tokenRepo.findByToken(request.token());
        if(verificationToken == null) {
            throw new EntityNotFoundException("Token not found");
        }
        if (!verificationToken.isVerified()){
            throw new EntityNotFoundException("Token not verified");
        }

        // 2. Check token expiration
        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(verificationToken);
            throw new IllegalStateException("Reset token has expired");
        }

        // 3. Get the associated user
        Users user = verificationToken.getUser();
        if (user == null) {
            throw new EntityNotFoundException("User associated with token not found");
        }

        // 4. Validate password match
        if (!request.isNewPasswordMatching()) {
            throw new IllegalArgumentException("New passwords don't match");
        }

        // 5. Check if new password is same as old
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // 6. Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);

        // 7. Delete token (prevent reuse)
        tokenRepo.delete(verificationToken);
    }

}
