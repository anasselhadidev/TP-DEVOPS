package com.Project_INTELLCAP.Infinitum_Art.Artwork.modele;

import com.Project_INTELLCAP.Infinitum_Art.Artist.modele.Artist;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "style")
@Data
public class Style {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "styles")
    private Set<Artwork> artworks = new HashSet<>();

    @ManyToMany(mappedBy = "styles")
    private Set<Artist> artists = new HashSet<>();
}
