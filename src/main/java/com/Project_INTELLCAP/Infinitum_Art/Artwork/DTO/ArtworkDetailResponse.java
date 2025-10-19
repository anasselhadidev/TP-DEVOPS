package com.Project_INTELLCAP.Infinitum_Art.Artwork.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ArtworkDetailResponse(
    Long id,
    String title,
    String imageUrl,
    BigDecimal price,
    LocalDateTime creationDate,
    Integer viewCount,
    Integer favoriteCount,
    String description,
    OwnerInfo owner,
    ArtistInfo artist
) {
    public static ArtworkDetailResponse fromArtwork(
            com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Artwork artwork,
            String imageUrl
    ) {
        return new ArtworkDetailResponse(
            artwork.getId(),
            artwork.getTitle(),
            imageUrl,
            artwork.getPrice(),
            artwork.getCreationDate(),
            artwork.getViewCount(),
            artwork.getFavoriteCount(),
            artwork.getDescription(),
            artwork.getOwner() != null ? OwnerInfo.fromUser(artwork.getOwner()) : null,
            artwork.getArtist() != null ? ArtistInfo.fromUser(artwork.getArtist()) : null
        );
    }

    public record OwnerInfo(Long id, String username) {
        public static OwnerInfo fromUser(com.Project_INTELLCAP.Infinitum_Art.user.modele.Users user) {
            return new OwnerInfo(user.getId(), user.getUsername());
        }
    }

    public record ArtistInfo(Long id, String username) {
        public static ArtistInfo fromUser(com.Project_INTELLCAP.Infinitum_Art.user.modele.Users user) {
            return new ArtistInfo(user.getId(), user.getUsername());
        }
    }
}
