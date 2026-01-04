package com.rdagdi.tasktrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rdagdi.tasktrack.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find User By Id
    Optional<User> findById(Long id);

    // Find User By Full Name
    List<User> findByFullName(String fullName);

    // Find User By Username
    List<User> findByUserName(String userName);

    // Find User By Email
    List<User> findByEmail(String email);

    // Find User By Role
    List<User> findByRole(String role);

    // Find User By Active
    List<User> findByActive(Boolean active);

    // Find User Created On
    List<User> findByCreatedAt(LocalDateTime createdAt);

    // Find User Updated On
    List<User> findByUpdatedAt(LocalDateTime updatedAt);

    // Find User Created Between
    List<User> findByCreatedAtBetween(LocalDateTime fromCreatedAt, LocalDateTime toCreatedAt);

    // Find User Updated Between
    List<User> findByUpdatedAtBetween(LocalDateTime fromUpdatedAt, LocalDateTime toUpdatedAt);

    // Find User Created Before
    List<User> findByCreatedAtBefore(LocalDateTime createdAt);

    // Find User Updated Before
    List<User> findByUpdatedAtBefore(LocalDateTime updatedAt);

    // Find User Created After
    List<User> findByCreatedAtAfter(LocalDateTime createdAt);

    // Find User Updated After
    List<User> findByUpdatedAtAfter(LocalDateTime updatedAt);

}
