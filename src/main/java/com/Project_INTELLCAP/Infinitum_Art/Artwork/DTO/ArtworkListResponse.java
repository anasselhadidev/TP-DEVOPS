package com.Project_INTELLCAP.Infinitum_Art.Artwork.DTO;

import org.springframework.data.domain.Page;

import java.util.List;

public record ArtworkListResponse(
        List<ArtworkListItemResponse> artworks,
        int currentPage,
        int totalPages,
        long totalElements,
        int size
) {
    public static ArtworkListResponse fromPage(Page<ArtworkListItemResponse> page) {
        return new ArtworkListResponse(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}
