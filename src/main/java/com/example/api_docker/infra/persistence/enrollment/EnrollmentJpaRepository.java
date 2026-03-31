package com.example.api_docker.infra.persistence.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, UUID> {
}
