package com.Project_INTELLCAP.Infinitum_Art.admin.modele;

import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Admin")
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "admin_Id")
    private Users user;
}
