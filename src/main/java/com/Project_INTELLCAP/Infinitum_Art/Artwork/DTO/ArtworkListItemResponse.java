package com.Project_INTELLCAP.Infinitum_Art.Artwork.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ArtworkListItemResponse(
    Long id,
    String title,
    String imageUrl,
    BigDecimal price,
    LocalDateTime creationDate,
    Integer viewCount,
    Integer favoriteCount,
    ArtistInfo artist
) {
    public static ArtworkListItemResponse fromArtwork(
            com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Artwork artwork,
            String imageUrl
    ) {
        return new ArtworkListItemResponse(
            artwork.getId(),
            artwork.getTitle(),
            imageUrl,
            artwork.getPrice(),
            artwork.getCreationDate(),
            artwork.getViewCount(),
            artwork.getFavoriteCount(),
            artwork.getArtist() != null ? ArtistInfo.fromUser(artwork.getArtist()) : null
        );
    }

    public record ArtistInfo(Long id, String username) {
        public static ArtistInfo fromUser(com.Project_INTELLCAP.Infinitum_Art.user.modele.Users user) {
            return new ArtistInfo(user.getId(), user.getUsername());
        }
    }
}
