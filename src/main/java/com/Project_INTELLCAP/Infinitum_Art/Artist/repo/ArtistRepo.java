package com.Project_INTELLCAP.Infinitum_Art.Artist.repo;

import com.Project_INTELLCAP.Infinitum_Art.Artist.modele.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepo extends JpaRepository<Artist, Long> {
}
