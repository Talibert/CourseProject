package com.example.api_docker.domain.certificate;

import com.example.api_docker.RepositoryAbstractTests;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CertificateRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private CertificateRepository certificateRepository;

    private Certificate buildCertificate() {
        return Certificate.restore(
                new CertificateId(UUID.randomUUID()),
                new EnrollmentId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                new CourseId(UUID.randomUUID()),
                "ABC123VERIFICATIONCODE",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve salvar e encontrar certificado pelo id")
    void shouldSaveAndFindCertificateById() {
        Certificate certificate = buildCertificate();
        certificateRepository.save(certificate);

        Optional<Certificate> found = certificateRepository.findById(certificate.getId());

        assertTrue(found.isPresent());
        assertEquals(certificate.getId(), found.get().getId());
        assertEquals(certificate.getEnrollmentId(), found.get().getEnrollmentId());
        assertEquals(certificate.getUserId(), found.get().getUserId());
        assertEquals(certificate.getCourseId(), found.get().getCourseId());
        assertEquals(certificate.getVerificationCode(), found.get().getVerificationCode());
        assertNotNull(found.get().getIssuedAt());
    }

    @Test
    @DisplayName("Deve retornar vazio quando certificado não encontrado pelo id")
    void shouldReturnEmptyWhenCertificateNotFoundById() {
        Optional<Certificate> found = certificateRepository.findById(
                new CertificateId(UUID.randomUUID())
        );

        assertFalse(found.isPresent());
    }
}