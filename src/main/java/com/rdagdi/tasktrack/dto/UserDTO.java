package com.rdagdi.tasktrack.dto;

import java.time.LocalDateTime;

import com.rdagdi.tasktrack.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for User entity.
 * This is what clients receive when requesting user data.
 * Decouples the API response from the internal entity structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private User.Role role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
