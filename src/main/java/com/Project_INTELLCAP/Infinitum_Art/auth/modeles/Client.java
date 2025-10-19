package com.Project_INTELLCAP.Infinitum_Art.auth.modeles;

import com.Project_INTELLCAP.Infinitum_Art.Commission.modele.Commission;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Users user;

    @OneToMany(mappedBy = "client")
    private List<Commission> commissions;

    // Default constructor for JPA
    public Client() {}

    // Constructor to link to a User
    public Client(Users user) {
        this.user = user;
    }


}
