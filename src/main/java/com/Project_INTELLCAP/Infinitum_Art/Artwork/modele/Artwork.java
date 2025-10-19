package com.Project_INTELLCAP.Infinitum_Art.Artwork.modele;

import com.Project_INTELLCAP.Infinitum_Art.Commission.modele.Commission;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Style;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "artwork")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_for_sale")
    private Boolean isForSale = false;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Users artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Users owner;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;

    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageVersion> imageVersions;

    @ManyToMany
    @JoinTable(
        name = "artwork_style",
        joinColumns = @JoinColumn(name = "artwork_id"),
        inverseJoinColumns = @JoinColumn(name = "style_id")
    )
    private Set<Style> styles = new HashSet<>();

    @OneToOne(mappedBy = "artwork")
    private Commission commission;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (viewCount == null) {
            viewCount = 0;
        }
        if (favoriteCount == null) {
            favoriteCount = 0;
        }
    }
}