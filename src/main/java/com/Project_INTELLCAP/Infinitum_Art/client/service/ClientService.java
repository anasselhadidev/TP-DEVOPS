package com.Project_INTELLCAP.Infinitum_Art.client.service;

import com.Project_INTELLCAP.Infinitum_Art.user.enums.UserRole;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.client.repo.ClientRepo;
import com.Project_INTELLCAP.Infinitum_Art.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepo clientRepo;
    private final UserRepo userRepo;

    @Transactional(readOnly = true)
    public Page<Users> getAllClients(int page, int size, Users adminUser) {
        validateAdmin(adminUser);
        return userRepo.findByRole(UserRole.CLIENT,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate")));
    }

    private void validateAdmin(Users user) {
        if (user == null || user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can access user lists");
        }
    }
}
