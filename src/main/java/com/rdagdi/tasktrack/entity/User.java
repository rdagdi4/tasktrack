package com.rdagdi.tasktrack.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false)
    private String userName;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @NotBlank
    // @Column
    private enum role {
        ADMIN, PROJECT_MANAGER, DEVELOPER, TESTER
    };

    @NotBlank
    @Column(nullable = false)
    private boolean active;

    @NotBlank
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotBlank
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

/*
 * Writing for my reference
 * Annotation Used :
 * 
 * @Id
 * 
 * @GeneratedValue
 * 
 * @NotBlank
 * 
 * @Column
 * 
 */