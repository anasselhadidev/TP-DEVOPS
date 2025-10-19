package com.Project_INTELLCAP.Infinitum_Art.Artwork.controller;

import com.Project_INTELLCAP.Infinitum_Art.Artwork.DTO.ArtworkDetailResponse;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.DTO.ArtworkListResponse;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.service.ArtworkService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArtworkController {
    
    private final ArtworkService artworkService;
    
    /**
     * Get top trending artworks for homepage
     * GET /api/homepage/top-this-week
     */
    @GetMapping("/homepage/top-this-week")
    public ResponseEntity<ArtworkListResponse> getTopTrendingArtworks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        
        ArtworkListResponse response = artworkService.getTopTrendingArtworks(page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get random artworks for discovery
     * GET /api/homepage/discover-more
     */
    @GetMapping("/homepage/discover-more")
    public ResponseEntity<ArtworkListResponse> getDiscoverMoreArtworks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        
        ArtworkListResponse response = artworkService.getDiscoverMoreArtworks(page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get detailed artwork information
     * GET /api/artwork/{artworkId}
     */
    @GetMapping("/artwork/{artworkId}")
    public ResponseEntity<ArtworkDetailResponse> getArtworkById(@PathVariable Long artworkId) {
        ArtworkDetailResponse response = artworkService.getArtworkById(artworkId);
        return ResponseEntity.ok(response);
    }

}
