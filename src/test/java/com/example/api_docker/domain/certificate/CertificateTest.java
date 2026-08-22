package com.example.api_docker.domain.certificate;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.certificate.event.CertificateIssuedEvent;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CertificateTest extends UnitAbstractTests {

    private EnrollmentId enrollmentId;
    private UserId userId;
    private CourseId courseId;
    private VerificationCodeGenerator verificationCodeGenerator;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        userId = UserId.generate();
        courseId = new CourseId(UUID.randomUUID());
        verificationCodeGenerator = () -> "CERT-123456";
    }

    @Test
    @DisplayName("Deve emitir certificado com sucesso e registrar evento CertificateIssuedEvent")
    void shouldIssueCertificateSuccessfullyWithEvent() {
        Certificate certificate = Certificate.issue(
                enrollmentId,
                userId,
                courseId,
                verificationCodeGenerator
        );

        assertNotNull(certificate.getId());
        assertEquals(enrollmentId, certificate.getEnrollmentId());
        assertEquals(userId, certificate.getUserId());
        assertEquals(courseId, certificate.getCourseId());
        assertEquals("CERT-123456", certificate.getVerificationCode());
        assertNotNull(certificate.getIssuedAt());

        List<DomainEvent> events = certificate.pullDomainEvents();
        assertEquals(1, events.size());

        CertificateIssuedEvent event = (CertificateIssuedEvent) events.getFirst();
        assertEquals(certificate.getId(), event.certificateId());
        assertEquals(userId, event.userId());
        assertEquals(courseId, event.courseId());

        assertTrue(certificate.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve restaurar certificado com sucesso sem registrar eventos de domínio")
    void shouldRestoreCertificateWithoutDomainEvents() {
        CertificateId id = new CertificateId(UUID.randomUUID());
        String verificationCode = "CERT-654321";
        LocalDateTime issuedAt = LocalDateTime.now().minusDays(5);

        Certificate certificate = Certificate.restore(
                id,
                enrollmentId,
                userId,
                courseId,
                verificationCode,
                issuedAt
        );

        assertEquals(id, certificate.getId());
        assertEquals(enrollmentId, certificate.getEnrollmentId());
        assertEquals(userId, certificate.getUserId());
        assertEquals(courseId, certificate.getCourseId());
        assertEquals(verificationCode, certificate.getVerificationCode());
        assertEquals(issuedAt, certificate.getIssuedAt());

        assertTrue(certificate.pullDomainEvents().isEmpty());
    }
}
