package com.example.api_docker.infra.persistence.certificate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CertificateJpaRepository extends JpaRepository<CertificateJpaEntity, UUID> {
}
