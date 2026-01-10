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
import com.rdagdi.tasktrack.dto.PagedResponse;
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
     * Get all users with pagination support.
     * 
     * <p>
     * <b>Endpoint:</b> GET /api/users
     * </p>
     * 
     * <h3>Query Parameters:</h3>
     * <table border="1">
     * <tr>
     * <th>Parameter</th>
     * <th>Type</th>
     * <th>Default</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>page</td>
     * <td>int</td>
     * <td>0</td>
     * <td>Zero-based page index. First page is 0.</td>
     * </tr>
     * <tr>
     * <td>size</td>
     * <td>int</td>
     * <td>10</td>
     * <td>Number of items per page (1-100).</td>
     * </tr>
     * <tr>
     * <td>sort</td>
     * <td>String</td>
     * <td>id,asc</td>
     * <td>Sorting: property,direction. Allowed: userName, email, fullName, role,
     * active, createdAt, updatedAt</td>
     * </tr>
     * </table>
     * 
     * <h3>Examples:</h3>
     * <ul>
     * <li>GET /api/users - First page, 10 users, default sort</li>
     * <li>GET /api/users?page=0&size=5 - First page, 5 users</li>
     * <li>GET /api/users?page=1&size=10 - Second page, 10 users</li>
     * <li>GET /api/users?sort=userName,asc - Sorted by username ascending</li>
     * <li>GET /api/users?sort=createdAt,desc - Sorted by creation date
     * descending</li>
     * <li>GET /api/users?page=0&size=10&sort=userName,asc - Combined</li>
     * </ul>
     * 
     * <h3>Response Structure:</h3>
     * 
     * <pre>
     * {
     *   "content": [...],       // Array of UserDTO objects
     *   "page": 0,              // Current page (0-indexed)
     *   "size": 10,             // Items per page
     *   "totalElements": 45,    // Total users in database
     *   "totalPages": 5,        // Total available pages
     *   "first": true,          // Is first page?
     *   "last": false           // Is last page?
     * }
     * </pre>
     * 
     * @param pageable Pagination info (auto-populated from query params by Spring)
     * @return Paginated response with users and metadata
     */
    @GetMapping
    public ResponseEntity<PagedResponse<UserDTO>> getAllUsers(
            @org.springframework.data.web.PageableDefault(size = 10, sort = "id") org.springframework.data.domain.Pageable pageable) {

        // Fetch paginated users from service
        org.springframework.data.domain.Page<User> userPage = userService.getAllUsers(pageable);

        // Convert entities to DTOs
        List<UserDTO> userDTOs = UserMapper.toDTOList(userPage.getContent());

        // Build paginated response with metadata
        PagedResponse<UserDTO> response = PagedResponse.<UserDTO>builder()
                .content(userDTOs)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();

        return ResponseEntity.ok(response);
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
