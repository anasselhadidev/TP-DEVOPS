package com.Project_INTELLCAP.Infinitum_Art.Commission.modele;

import com.Project_INTELLCAP.Infinitum_Art.Artist.modele.Artist;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Artwork;
import com.Project_INTELLCAP.Infinitum_Art.Artwork.modele.Style;
import com.Project_INTELLCAP.Infinitum_Art.Commission.enums.CommissionStatus;
import com.Project_INTELLCAP.Infinitum_Art.auth.modeles.Client;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission")
@Data
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private CommissionStatus status;

    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @OneToOne
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private Style style;
}
