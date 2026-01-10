package com.rdagdi.tasktrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rdagdi.tasktrack.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ========== Basic Finders ==========

    // Find by unique fields (return Optional since they're unique)
    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    // ========== Existence Checks ==========

    // Check if username/email already exists (for validation)
    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    // ========== Query by Status & Role ==========

    // Find all active/inactive users
    List<User> findByActive(Boolean active);

    // Find users by role
    List<User> findByRole(User.Role role);

    // Find active users with specific role (combined query)
    List<User> findByActiveAndRole(Boolean active, User.Role role);

    // ========== Search & Filtering ==========

    // Find users by partial name match (useful for search)
    List<User> findByFullNameContainingIgnoreCase(String name);

    // ========== Date Range Queries ==========

    // Find users created within a date range (for reports)
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find recently created users
    List<User> findByCreatedAtAfter(LocalDateTime since);

    // ========== Analytics/Stats ==========

    // Count users by role (for dashboard)
    long countByRole(User.Role role);

    // Count active users
    long countByActive(Boolean active);
}