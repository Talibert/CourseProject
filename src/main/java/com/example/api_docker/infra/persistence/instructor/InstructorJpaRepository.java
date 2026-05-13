package com.example.api_docker.infra.persistence.instructor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstructorJpaRepository extends JpaRepository<InstructorJpaEntity, UUID> {
    Optional<InstructorJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
