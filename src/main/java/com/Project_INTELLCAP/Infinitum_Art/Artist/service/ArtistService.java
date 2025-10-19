package com.Project_INTELLCAP.Infinitum_Art.Artist.service;

import com.Project_INTELLCAP.Infinitum_Art.Artist.repo.ArtistRepo;
import com.Project_INTELLCAP.Infinitum_Art.Artist.DTO.ArtistRequest;
import com.Project_INTELLCAP.Infinitum_Art.Artist.modele.Artist;
import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Token;
import com.Project_INTELLCAP.Infinitum_Art.auth.repo.TokenRepo;
import com.Project_INTELLCAP.Infinitum_Art.auth.services.EmailService;
import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ArtistRepo artistRepo;
    private final TokenRepo tokenRepo;
    private final EmailService emailService;


    @PreAuthorize("hasRole('ADMIN')")
    public void createArtist(ArtistRequest request, Users  currentUser) {

        if (request.role() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot create admin accounts");
        }

        if (userRepo.existsByEmail(request.email())) {
            throw new DataIntegrityViolationException("Email already registered");
        }

        if (request.role() == UserRole.ARTIST) {
            if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
                throw new AccessDeniedException("Only admin can create artist accounts");
            }
        } else if (request.role() != UserRole.CLIENT) {
            throw new AccessDeniedException("Invalid role for user creation");
        }


        Users newUser = new Users();
        newUser.setUsername(request.fullName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(UserRole.ARTIST); // Force artist role
        newUser.setCreationDate(LocalDateTime.now());

        newUser = userRepo.save(newUser);
        artistRepo.save(new Artist(newUser));

        Token token = new Token(newUser);
        tokenRepo.save(token);
        emailService.sendVerificationEmail(newUser, token.getToken());
    }

    @Transactional(readOnly = true)
    public Page<Users> getAllArtists(int page, int size, Users adminUser) {
        if (adminUser == null || adminUser.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can access user lists");
        }        return userRepo.findByRole(UserRole.ARTIST,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate")));
    }
}
