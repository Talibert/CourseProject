package com.example.api_docker.infra.persistence.instructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "instructors")
@Getter
@Setter
public class InstructorJpaEntity {

    @Id
    @Column(name = "instructor_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "bio")
    private String bio;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "github")
    private String github;

    @Column(name = "youtube")
    private String youtube;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected InstructorJpaEntity() {}
}
