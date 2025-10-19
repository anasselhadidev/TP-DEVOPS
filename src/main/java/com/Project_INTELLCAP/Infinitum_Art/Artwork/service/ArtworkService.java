package com.Project_INTELLCAP.Infinitum_Art.Artwork.service;

import com.Project_INTELLCAP.Infinitum_Art.Artwork.DTO.*;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Artwork;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.ImageVersion;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.enums.ImageType;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.repo.ArtworkRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepo artworkRepo;

    @Transactional(readOnly = true)
    public ArtworkListResponse getTopTrendingArtworks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Artwork> artworksPage = artworkRepo.findTopTrendingArtworksForSale(pageable);
        Page<ArtworkListItemResponse> responsePage = artworksPage.map(artwork -> {
            String imageUrl = getWatermarkedSmallImageUrl(artwork);
            return ArtworkListItemResponse.fromArtwork(artwork, imageUrl);
        });
        return ArtworkListResponse.fromPage(responsePage);
    }

    @Transactional(readOnly = true)
    public ArtworkListResponse getDiscoverMoreArtworks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Artwork> artworksPage = artworkRepo.findRandomArtworksForSale(pageable);
        Page<ArtworkListItemResponse> responsePage = artworksPage.map(artwork -> {
            String imageUrl = getWatermarkedSmallImageUrl(artwork);
            return ArtworkListItemResponse.fromArtwork(artwork, imageUrl);
        });
        return ArtworkListResponse.fromPage(responsePage);
    }

    @Transactional
    public ArtworkDetailResponse getArtworkById(Long artworkId) {
        Optional<Artwork> artworkOpt = artworkRepo.findById(artworkId);

        if (artworkOpt.isEmpty() || !artworkOpt.get().getIsForSale()) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Artwork not found with id: " + artworkId
            );
        }

        Artwork artwork = artworkOpt.get();
        artwork.setViewCount(artwork.getViewCount() + 1);
        artworkRepo.save(artwork);
        String imageUrl = getWatermarkedLargeImageUrl(artwork);
        return ArtworkDetailResponse.fromArtwork(artwork, imageUrl);
    }

    public String getWatermarkedSmallImageUrl(Artwork artwork) {
        if (artwork.getImageVersions() == null || artwork.getImageVersions().isEmpty()) {
            return null;
        }
        return artwork.getImageVersions().stream()
                .filter(img -> img.getImageType() == ImageType.WATERMARKED_SMALL)
                .findFirst()
                .map(ImageVersion::getImageUrl)
                .orElse(null);
    }

    public String getWatermarkedLargeImageUrl(Artwork artwork) {
        if (artwork.getImageVersions() == null || artwork.getImageVersions().isEmpty()) {
            return null;
        }
        return artwork.getImageVersions().stream()
                .filter(img -> img.getImageType() == ImageType.WATERMARKED_LARGE)
                .findFirst()
                .map(ImageVersion::getImageUrl)
                .orElse(null);
    }
}