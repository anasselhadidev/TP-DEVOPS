package com.Project_INTELLCAP.Infinitum_Art.Artist.modele;

import com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Style;
import com.Project_INTELLCAP.Infinitum_Art.Commission.modele.Commission;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Artist")
@Data
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "artist_Id")
    private Users user;

    @ManyToMany
    @JoinTable(
            name = "artist_style",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id")
    )
    private Set<Style> styles = new HashSet<>();

    @OneToMany(mappedBy = "artist")
    private List<Commission> commissions;

    // Custom constructor for Users
    public Artist(Users user) {
        this.user = user;
    }

    // Default constructor (required by JPA)
    public Artist() {}
}
