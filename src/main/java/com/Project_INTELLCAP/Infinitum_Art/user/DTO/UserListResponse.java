package com.Project_INTELLCAP.Infinitum_Art.user.DTO;

import com.Project_INTELLCAP.Infinitum_Art.user.modele.Users;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserListResponse(
        List<UserProfileResponse> users,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static UserListResponse fromPage(Page<com.Project_INTELLCAP.Infinitum_Art.user.modele.Users> page) {
        List<UserProfileResponse> users = page.getContent().stream()
                .map(user -> new UserProfileResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getRole(),
                        user.getCreationDate(),
                        user.getImageUrl(),
                        user.getPhoneNumber()
                ))
                .toList();

        return new UserListResponse(
                users,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}