package com.rdagdi.tasktrack.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.rdagdi.tasktrack.entity.User;

/**
 * Utility class for converting between User entity and DTOs.
 * Provides static methods for mapping in both directions.
 */
public class UserMapper {

    // Private constructor to prevent instantiation
    private UserMapper() {
    }

    /**
     * Convert User entity to UserDTO
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of User entities to list of UserDTOs
     */
    public static List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert CreateUserRequest DTO to User entity
     */
    public static User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        // active defaults to true in entity
        return user;
    }

    /**
     * Update existing User entity with UpdateUserRequest data
     */
    public static void updateEntity(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return;
        }

        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setActive(request.getActive());
    }
}
