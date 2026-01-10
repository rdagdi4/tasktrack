package com.rdagdi.tasktrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rdagdi.tasktrack.dto.CreateUserRequest;
import com.rdagdi.tasktrack.dto.UpdateUserRequest;
import com.rdagdi.tasktrack.dto.UserDTO;
import com.rdagdi.tasktrack.dto.UserMapper;
import com.rdagdi.tasktrack.entity.User;
import com.rdagdi.tasktrack.service.UserService;

import jakarta.validation.Valid;

/**
 * REST Controller for User management.
 * Uses DTOs for request/response to decouple API from entity structure.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = UserMapper.toEntity(request);
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(UserMapper.toDTO(createdUser), HttpStatus.CREATED);
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(UserMapper.toDTOList(users));
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    /**
     * Update user by ID
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        // Get existing user and apply updates
        User existingUser = userService.getUserById(id);
        UserMapper.updateEntity(existingUser, request);

        User updatedUser = userService.updateUser(id, existingUser);
        return ResponseEntity.ok(UserMapper.toDTO(updatedUser));
    }

    /**
     * Soft delete user by ID (deactivate)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
        User deactivatedUser = userService.deleteUser(id);
        return ResponseEntity.ok(UserMapper.toDTO(deactivatedUser));
    }

    /**
     * Reactivate user by ID
     * PUT /api/users/{id}/reactivate
     */
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<UserDTO> reactivateUser(@PathVariable Long id) {
        User reactivatedUser = userService.reactivateUser(id);
        return ResponseEntity.ok(UserMapper.toDTO(reactivatedUser));
    }

    /**
     * Get all active users
     * GET /api/users/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        List<User> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(UserMapper.toDTOList(users));
    }

    /**
     * Get users by role
     * GET /api/users/role/{role}
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable User.Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(UserMapper.toDTOList(users));
    }
}
