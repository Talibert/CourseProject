package com.example.api_docker.infra.persistence.certificate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

// infra/persistence/certificate/CertificateJpaEntity.java
@Entity
@Table(name = "certificates")
@Getter
@Setter
public class CertificateJpaEntity {

    @Id
    @Column(name = "certificate_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "enrollment_id", nullable = false, columnDefinition = "uuid")
    private UUID enrollmentId;

    @Column(name = "student_id", nullable = false, columnDefinition = "uuid")
    private UUID studentId;

    @Column(name = "course_id", nullable = false, columnDefinition = "uuid")
    private UUID courseId;

    @Column(name = "verification_code", nullable = false, unique = true)
    private String verificationCode;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    protected CertificateJpaEntity() {}
}
