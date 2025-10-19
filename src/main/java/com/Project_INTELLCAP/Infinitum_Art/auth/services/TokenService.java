package com.Project_INTELLCAP.Infinitum_Art.auth.services;

import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Token;
import com.Project_INTELLCAP.Infinitum_Art.auth.repo.TokenRepo;
import com.Project_INTELLCAP.Infinitum_Art.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepo tokenRepo;
    private final AuthService authService;
    private final UserRepo userRepo;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);


    @Transactional
    public void verify(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Verification token cannot be empty");
        }

        Token verificationToken = tokenRepo.findByToken(token);
        System.out.println("Token: " + verificationToken);
        if (verificationToken == null) {
            throw new IllegalArgumentException("Invalid verification token");
        }

        if (verificationToken.isExpired()) {
            tokenRepo.delete(verificationToken);
            throw new IllegalStateException("Verification token has expired");
        }

        authService.setUserVerified(verificationToken.getUser());
        tokenRepo.delete(verificationToken);
    }

    @Transactional
    public void resendVerificationToken(String email) {
        Users user = userRepo.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        if (user.isActive()) {
            throw new IllegalStateException("User is already verified");
        }

        tokenRepo.deleteByUser(user);
        Token newToken = new Token(user);
        tokenRepo.save(newToken);
        emailService.sendVerificationEmail(user, newToken.getToken());
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupTokens() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Delete all expired tokens (regardless of age)
        int expiredCount = tokenRepo.deleteByExpiryDateBefore(now);

        // 2. Delete very old unconfirmed tokens (30+ days old)
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        int oldUnconfirmedCount = tokenRepo.deleteByCreatedAtBeforeAndUser_ActiveFalse(thirtyDaysAgo);

        if (expiredCount > 0 || oldUnconfirmedCount > 0) {
            logger.info("Token cleanup: {} expired tokens and {} old unconfirmed tokens removed",
                    expiredCount, oldUnconfirmedCount);
        }
    }


    public String validateResetToken(String tokenValue) {
        Token token = tokenRepo.findByToken(tokenValue);
        if (token == null) {
            throw new IllegalArgumentException("Token not found");
        }

        if (token.isExpired()) {
            throw new IllegalArgumentException("Token has expired");
        }

        if (token.isVerified()) {
            throw new IllegalArgumentException("Token has already been used");
        }
        token.setVerified(true);
        tokenRepo.save(token);
        return  token.getToken();
    }


}