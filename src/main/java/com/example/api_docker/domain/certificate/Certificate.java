package com.example.api_docker.domain.certificate;

import com.example.api_docker.domain.certificate.event.CertificateIssuedEvent;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Certificate {

    private final CertificateId id;
    private final EnrollmentId enrollmentId;
    private final UserId userId;
    private final CourseId courseId;
    private final String verificationCode;
    private final LocalDateTime issuedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Certificate(CertificateId id, EnrollmentId enrollmentId,
                        UserId userId, CourseId courseId,
                        String verificationCode) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.verificationCode = verificationCode;
        this.issuedAt = LocalDateTime.now();
    }

    private Certificate(CertificateId id, EnrollmentId enrollmentId,
                        UserId userId, CourseId courseId,
                        String verificationCode, LocalDateTime issuedAt) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.verificationCode = verificationCode;
        this.issuedAt = issuedAt;
    }

    public static Certificate issue(EnrollmentId enrollmentId,
                                    UserId userId, CourseId courseId,
                                    VerificationCodeGenerator verificationCodeGenerator) {
        Certificate certificate = new Certificate(
                CertificateId.generate(),
                enrollmentId,
                userId,
                courseId,
                verificationCodeGenerator.generate()
        );

        certificate.domainEvents.add(new CertificateIssuedEvent(certificate.id, userId, courseId));

        return certificate;
    }

    public static Certificate restore(CertificateId id, EnrollmentId enrollmentId,
                                      UserId userId, CourseId courseId,
                                      String verificationCode, LocalDateTime issuedAt) {
        return new Certificate(id, enrollmentId, userId,
                courseId, verificationCode, LocalDateTime.now());
    }

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
}
