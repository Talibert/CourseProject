package com.example.api_docker.domain.certificate;

import java.util.Optional;

public interface CertificateRepository {
    void save(Certificate certificate);
    Optional<Certificate> findById(CertificateId id);
}
