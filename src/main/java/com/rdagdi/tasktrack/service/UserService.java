package com.rdagdi.tasktrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rdagdi.tasktrack.repository.UserRepository;
import com.rdagdi.tasktrack.entity.User;
import com.rdagdi.tasktrack.exception.UserNotFoundException;
import com.rdagdi.tasktrack.exception.DuplicateUserException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // ========== CREATE ==========

    /**
     * Create a new user with validation
     * Validates: username uniqueness, email uniqueness
     *
     * @param user The user to create
     * @return The created user
     * @throws DuplicateUserException if username or email already exists
     */
    public User createUser(User user) {
        logger.info("Creating new user with username: {}", user.getUserName());

        // Validate username uniqueness
        if (userRepository.existsByUserName(user.getUserName())) {
            logger.warn("Username already exists: {}", user.getUserName());
            throw new DuplicateUserException("Username already exists: " + user.getUserName());
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: {}", user.getEmail());
            throw new DuplicateUserException("Email already exists: " + user.getEmail());
        }

        // Save the user
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    // ========== READ ==========

    /**
     * Get user by ID
     *
     * @param id The user ID
     * @return The found user
     * @throws UserNotFoundException if user not found
     */
    public User getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Get user by username
     *
     * @param username The username
     * @return The found user
     * @throws UserNotFoundException if user not found
     */
    public User getUserByUsername(String username) {
        logger.debug("Fetching user with username: {}", username);

        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    /**
     * Get user by email
     *
     * @param email The email
     * @return The found user
     * @throws UserNotFoundException if user not found
     */
    public User getUserByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Get all users (non-paginated)
     *
     * @return List of all users (including inactive)
     */
    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Get all users with pagination support.
     * 
     * <p>
     * <b>Query Parameters:</b>
     * </p>
     * <ul>
     * <li><b>page</b> - Zero-based page index (0 = first page). Default: 0</li>
     * <li><b>size</b> - Number of items per page. Default: 10, Max: 100</li>
     * <li><b>sort</b> - Sorting criteria: property,direction (e.g.,
     * "userName,asc")</li>
     * </ul>
     * 
     * <p>
     * <b>Example:</b> GET /api/users?page=0&size=10&sort=createdAt,desc
     * </p>
     *
     * @param pageable Pagination and sorting information from request
     * @return Page of users with pagination metadata
     */
    public org.springframework.data.domain.Page<User> getAllUsers(org.springframework.data.domain.Pageable pageable) {
        logger.debug("Fetching users - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable);
    }

    /**
     * Get all active users
     *
     * @return List of active users
     */
    public List<User> getAllActiveUsers() {
        logger.debug("Fetching all active users");
        return userRepository.findByActive(true);
    }

    /**
     * Get users by role
     *
     * @param role The user role
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(User.Role role) {
        logger.debug("Fetching users with role: {}", role);
        return userRepository.findByRole(role);
    }

    // ========== UPDATE ==========

    /**
     * Update an existing user
     * Note: Some fields like createdAt cannot be updated (protected by JPA)
     *
     * @param id          The user ID to update
     * @param updatedUser The user object with updated data
     * @return The updated user
     * @throws UserNotFoundException  if user not found
     * @throws DuplicateUserException if username/email conflicts with another user
     */
    public User updateUser(Long id, User updatedUser) {
        logger.info("Updating user with ID: {}", id);

        // Check if user exists
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Validate username uniqueness (if changed)
        if (!existingUser.getUserName().equals(updatedUser.getUserName())) {
            if (userRepository.existsByUserName(updatedUser.getUserName())) {
                logger.warn("Username already exists: {}", updatedUser.getUserName());
                throw new DuplicateUserException("Username already exists: " + updatedUser.getUserName());
            }
        }

        // Validate email uniqueness (if changed)
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                logger.warn("Email already exists: {}", updatedUser.getEmail());
                throw new DuplicateUserException("Email already exists: " + updatedUser.getEmail());
            }
        }

        // Update fields
        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setActive(updatedUser.getActive());
        // Note: createdAt is not updated (immutable field)
        // Note: updatedAt is automatically updated by @UpdateTimestamp

        User savedUser = userRepository.save(existingUser);
        logger.info("User updated successfully: {}", id);

        return savedUser;
    }

    // ========== DELETE ==========

    /**
     * Soft delete - deactivates user instead of permanently deleting
     * This is the recommended approach for production systems
     *
     * @param id The user ID to deactivate
     * @return The deactivated user
     * @throws UserNotFoundException if user not found
     */
    public User deleteUser(Long id) {
        logger.info("Soft deleting (deactivating) user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(false);
        User deactivatedUser = userRepository.save(user);

        logger.info("User deactivated successfully: {}", id);
        return deactivatedUser;
    }

    /**
     * Hard delete - permanently removes user from database
     * WARNING: Use with caution! This cannot be undone.
     * Only use for testing or compliance (GDPR deletion requests)
     *
     * @param id The user ID to permanently delete
     * @throws UserNotFoundException if user not found
     */
    public void hardDeleteUser(Long id) {
        logger.warn("HARD DELETING user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        logger.warn("User permanently deleted: {}", id);
    }

    /**
     * Reactivate a soft-deleted (inactive) user
     *
     * @param id The user ID to reactivate
     * @return The reactivated user
     * @throws UserNotFoundException if user not found
     */
    public User reactivateUser(Long id) {
        logger.info("Reactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(true);
        User reactivatedUser = userRepository.save(user);

        logger.info("User reactivated successfully: {}", id);
        return reactivatedUser;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Check if a username is available
     *
     * @param username The username to check
     * @return true if available, false if taken
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUserName(username);
    }

    /**
     * Check if an email is available
     *
     * @param email The email to check
     * @return true if available, false if taken
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Get count of users by role
     *
     * @param role The role to count
     * @return Number of users with that role
     */
    public long countUsersByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    /**
     * Get count of active users
     *
     * @return Number of active users
     */
    public long countActiveUsers() {
        return userRepository.countByActive(true);
    }
}

/*
 * DESIGN DECISIONS & BEST PRACTICES:
 * 
 * 1. DTOs vs Entities:
 * - Current: Returning entities directly for simplicity
 * - Production: Should create DTOs (UserDTO, CreateUserRequest,
 * UpdateUserRequest)
 * - Why DTOs: Decouple API from database, hide sensitive fields, control what's
 * exposed
 * 
 * 2. Soft Delete vs Hard Delete:
 * - Chosen: Soft delete as default (set active = false)
 * - Why: Audit trail, data recovery, compliance, referential integrity
 * - Hard delete available but should be used cautiously
 * 
 * 3. Validation:
 * - Service layer validates business rules (uniqueness, existence)
 * - Entity layer validates data format (@NotBlank, @Email, etc.)
 * - Both layers work together for complete validation
 * 
 * 4. Exception Handling:
 * - Custom exceptions (UserNotFoundException, DuplicateUserException)
 * - Better than returning null or generic exceptions
 * - Controller layer should handle these and return proper HTTP responses
 * 
 * 5. Logging:
 * - Using SLF4J logger instead of System.out.println
 * - Different levels: debug, info, warn, error
 * - Production-ready logging for monitoring and debugging
 */
