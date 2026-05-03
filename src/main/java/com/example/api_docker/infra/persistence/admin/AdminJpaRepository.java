package com.example.api_docker.infra.persistence.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, UUID> {
    Optional<AdminJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
