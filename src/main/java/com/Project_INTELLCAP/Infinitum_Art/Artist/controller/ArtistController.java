package com.Project_INTELLCAP.Infinitum_Art.Artist.controller;


import com.Project_INTELLCAP.Infinitum_Art.Artist.DTO.ArtistRequest;
import com.Project_INTELLCAP.Infinitum_Art.Artist.service.ArtistService;
import com.Project_INTELLCAP.Infinitum_Art.auth.annotations.CurrentUser;
import com.Project_INTELLCAP.Infinitum_Art.user.DTO.UserListResponse;
import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ArtistController {
    private final ArtistService artistService;


    @PostMapping("/artist")
    public ResponseEntity<?> createArtist(@RequestBody ArtistRequest request, @CurrentUser Users currentUser) {
        if(request.role() != UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role");
        }

        artistService.createArtist(request,currentUser);
        return ResponseEntity.ok("message: user created successfully");
    }

    @GetMapping("/artists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getAllArtists(
            @CurrentUser Users currentUser,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        Page<Users> artistsPage = artistService.getAllArtists(page, size, currentUser);
        return ResponseEntity.ok(UserListResponse.fromPage(artistsPage));
    }
}
