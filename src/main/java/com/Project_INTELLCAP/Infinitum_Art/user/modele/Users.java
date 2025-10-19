package com.Project_INTELLCAP.Infinitum_Art.user.modele;

import com.Project_INTELLCAP.Infinitum_Art.user.DTO.UserUpdateResponse;
import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String Username;

    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "image_url")
    private String imageUrl;

    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private boolean active =false;
    private boolean locked =true;
    private boolean expired =true;
    private boolean credentialsExpired ;
    private boolean isEmailVerified = false;




    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Users users = (Users) o;
        return id != null && id.equals(users.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
