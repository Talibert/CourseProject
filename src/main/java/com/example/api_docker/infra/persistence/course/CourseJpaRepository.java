package com.example.api_docker.infra.persistence.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, UUID> {
}
