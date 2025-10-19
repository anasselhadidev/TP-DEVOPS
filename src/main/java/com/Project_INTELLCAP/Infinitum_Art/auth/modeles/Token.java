package com.Project_INTELLCAP.Infinitum_Art.auth.modeles;

import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store hashed version of token (Base64-encoded)
    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "new_email")
    private String newEmail;

    public Token() {
        this.createdAt = LocalDateTime.now();
        this.token = UUID.randomUUID().toString();
        this.verified = false;
    }

    public Token(Users user) {
        this(); // calls above, sets hashed token
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(60);
        this.verified = false;
    }


    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }


}
