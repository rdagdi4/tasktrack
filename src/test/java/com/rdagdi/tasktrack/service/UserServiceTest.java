package com.rdagdi.tasktrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rdagdi.tasktrack.entity.User;
import com.rdagdi.tasktrack.entity.User.Role;
import com.rdagdi.tasktrack.exception.DuplicateUserException;
import com.rdagdi.tasktrack.exception.UserNotFoundException;
import com.rdagdi.tasktrack.repository.UserRepository;

/**
 * Unit tests for UserService.
 * Uses Mockito to mock UserRepository dependency.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(Role.DEVELOPER);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    // ========== CREATE USER TESTS ==========

    @Nested
    @DisplayName("createUser Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully when username and email are unique")
        void createUser_Success() {
            // Arrange
            when(userRepository.existsByUserName(testUser.getUserName())).thenReturn(false);
            when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.createUser(testUser);

            // Assert
            assertNotNull(result);
            assertEquals(testUser.getUserName(), result.getUserName());
            assertEquals(testUser.getEmail(), result.getEmail());
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw DuplicateUserException when username already exists")
        void createUser_DuplicateUsername() {
            // Arrange
            when(userRepository.existsByUserName(testUser.getUserName())).thenReturn(true);

            // Act & Assert
            DuplicateUserException exception = assertThrows(
                    DuplicateUserException.class,
                    () -> userService.createUser(testUser));

            assertTrue(exception.getMessage().contains("Username already exists"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw DuplicateUserException when email already exists")
        void createUser_DuplicateEmail() {
            // Arrange
            when(userRepository.existsByUserName(testUser.getUserName())).thenReturn(false);
            when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

            // Act & Assert
            DuplicateUserException exception = assertThrows(
                    DuplicateUserException.class,
                    () -> userService.createUser(testUser));

            assertTrue(exception.getMessage().contains("Email already exists"));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    // ========== GET USER BY ID TESTS ==========

    @Nested
    @DisplayName("getUserById Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when found")
        void getUserById_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            User result = userService.getUserById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(testUser.getId(), result.getId());
            assertEquals(testUser.getUserName(), result.getUserName());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void getUserById_NotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.getUserById(999L));

            assertTrue(exception.getMessage().contains("999"));
        }
    }

    // ========== GET ALL USERS TESTS ==========

    @Nested
    @DisplayName("getAllUsers Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return list of all users")
        void getAllUsers_Success() {
            // Arrange
            User user2 = new User();
            user2.setId(2L);
            user2.setUserName("user2");

            when(userRepository.findAll()).thenReturn(List.of(testUser, user2));

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void getAllUsers_Empty() {
            // Arrange
            when(userRepository.findAll()).thenReturn(List.of());

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    // ========== UPDATE USER TESTS ==========

    @Nested
    @DisplayName("updateUser Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void updateUser_Success() {
            // Arrange
            User updatedData = new User();
            updatedData.setUserName("updateduser");
            updatedData.setEmail("updated@example.com");
            updatedData.setFullName("Updated Name");
            updatedData.setRole(Role.PROJECT_MANAGER);
            updatedData.setActive(true);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUserName("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            User result = userService.updateUser(1L, updatedData);

            // Assert
            assertEquals("updateduser", result.getUserName());
            assertEquals("updated@example.com", result.getEmail());
            assertEquals("Updated Name", result.getFullName());
            assertEquals(Role.PROJECT_MANAGER, result.getRole());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user to update doesn't exist")
        void updateUser_NotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.updateUser(999L, testUser));
        }

        @Test
        @DisplayName("Should throw DuplicateUserException when new username conflicts")
        void updateUser_DuplicateUsername() {
            // Arrange
            User updatedData = new User();
            updatedData.setUserName("existinguser");
            updatedData.setEmail(testUser.getEmail());

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUserName("existinguser")).thenReturn(true);

            // Act & Assert
            assertThrows(
                    DuplicateUserException.class,
                    () -> userService.updateUser(1L, updatedData));
        }

        @Test
        @DisplayName("Should allow updating with same username (no change)")
        void updateUser_SameUsername() {
            // Arrange
            User updatedData = new User();
            updatedData.setUserName(testUser.getUserName()); // Same username
            updatedData.setEmail("newemail@example.com");
            updatedData.setFullName("Updated Name");
            updatedData.setRole(Role.DEVELOPER);
            updatedData.setActive(true);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            User result = userService.updateUser(1L, updatedData);

            // Assert
            assertEquals(testUser.getUserName(), result.getUserName());
            verify(userRepository, never()).existsByUserName(any());
        }
    }

    // ========== DELETE USER TESTS ==========

    @Nested
    @DisplayName("deleteUser (soft delete) Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should soft delete (deactivate) user successfully")
        void deleteUser_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            User result = userService.deleteUser(1L);

            // Assert
            assertFalse(result.getActive());
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user to delete doesn't exist")
        void deleteUser_NotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.deleteUser(999L));
        }
    }

    // ========== REACTIVATE USER TESTS ==========

    @Nested
    @DisplayName("reactivateUser Tests")
    class ReactivateUserTests {

        @Test
        @DisplayName("Should reactivate user successfully")
        void reactivateUser_Success() {
            // Arrange
            testUser.setActive(false); // User is currently inactive
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            User result = userService.reactivateUser(1L);

            // Assert
            assertTrue(result.getActive());
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user to reactivate doesn't exist")
        void reactivateUser_NotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.reactivateUser(999L));
        }
    }

    // ========== UTILITY METHODS TESTS ==========

    @Nested
    @DisplayName("Utility Methods Tests")
    class UtilityMethodsTests {

        @Test
        @DisplayName("isUsernameAvailable should return true when username not taken")
        void isUsernameAvailable_True() {
            when(userRepository.existsByUserName("newuser")).thenReturn(false);
            assertTrue(userService.isUsernameAvailable("newuser"));
        }

        @Test
        @DisplayName("isUsernameAvailable should return false when username taken")
        void isUsernameAvailable_False() {
            when(userRepository.existsByUserName("existinguser")).thenReturn(true);
            assertFalse(userService.isUsernameAvailable("existinguser"));
        }

        @Test
        @DisplayName("isEmailAvailable should return true when email not taken")
        void isEmailAvailable_True() {
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            assertTrue(userService.isEmailAvailable("new@example.com"));
        }

        @Test
        @DisplayName("countUsersByRole should return correct count")
        void countUsersByRole_Success() {
            when(userRepository.countByRole(Role.DEVELOPER)).thenReturn(5L);
            assertEquals(5L, userService.countUsersByRole(Role.DEVELOPER));
        }

        @Test
        @DisplayName("countActiveUsers should return correct count")
        void countActiveUsers_Success() {
            when(userRepository.countByActive(true)).thenReturn(10L);
            assertEquals(10L, userService.countActiveUsers());
        }
    }
}
