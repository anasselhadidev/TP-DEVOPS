package com.Project_INTELLCAP.Infinitum_Art.Artwork.repo;

import com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Artwork;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtworkRepo extends JpaRepository<Artwork, Long> {

    @Query("SELECT a FROM Artwork a WHERE a.isForSale = true " +
            "ORDER BY (a.viewCount * 0.3 + a.favoriteCount * 0.7) DESC")
    Page<Artwork> findTopTrendingArtworksForSale(Pageable pageable);

    @Query(value = "SELECT * FROM artwork WHERE is_for_sale = true ORDER BY RANDOM()", nativeQuery = true)
    Page<Artwork> findRandomArtworksForSale(Pageable pageable);

    Optional<Artwork> findById(Long id);
}
