package com.Project_INTELLCAP.Infinitum_Art.client.controller;

import com.Project_INTELLCAP.Infinitum_Art.auth.annotations.CurrentUser;
import com.Project_INTELLCAP.Infinitum_Art.client.service.ClientService;
import com.Project_INTELLCAP.Infinitum_Art.user.DTO.UserListResponse;
import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import com.Project_INTELLCAP.Infinitum_Art.user.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClientController {
    private final ClientService clientService;

    @GetMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getAllClients(
            @CurrentUser Users currentUser,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        Page<Users> clientsPage = clientService.getAllClients(page, size, currentUser);
        return ResponseEntity.ok(UserListResponse.fromPage(clientsPage));
    }


}
