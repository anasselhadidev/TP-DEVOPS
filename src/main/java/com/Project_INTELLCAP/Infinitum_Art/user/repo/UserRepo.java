package com.Project_INTELLCAP.Infinitum_Art.user.repo;

import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Users> findByRole(UserRole role, Pageable pageable);

}
