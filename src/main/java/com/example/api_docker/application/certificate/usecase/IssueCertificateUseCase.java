package com.example.api_docker.application.certificate.usecase;

import com.example.api_docker.application.certificate.command.IssueCertificateCommand;
import com.example.api_docker.domain.certificate.Certificate;
import com.example.api_docker.domain.certificate.CertificateRepository;
import com.example.api_docker.domain.certificate.VerificationCodeGenerator;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IssueCertificateUseCase {

    private final CertificateRepository certificateRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final DomainEventPublisher domainEventPublisher;

    public void execute(IssueCertificateCommand command) {
        Certificate certificate = Certificate.issue(
                command.enrollmentId(),
                command.studentId(),
                command.courseId(),
                verificationCodeGenerator
        );

        certificateRepository.save(certificate);
        certificate.pullDomainEvents().forEach(domainEventPublisher::publish);
    }
}
