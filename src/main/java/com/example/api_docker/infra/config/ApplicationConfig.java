package com.example.api_docker.infra.config;

import com.example.api_docker.application.certificate.usecase.IssueCertificateUseCase;
import com.example.api_docker.application.student.usecase.*;
import com.example.api_docker.domain.certificate.CertificateRepository;
import com.example.api_docker.domain.certificate.VerificationCodeGenerator;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.PasswordEncoder;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.TokenGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IMPORTANTE: estamos definindo os useCases como beans manualmente para manter o principio da clean arch.
 * A ideia é que o useCase não conheça o framework
 */
@Configuration
public class ApplicationConfig {


    // ─── Certificate ───────────────────────────────────────────

    @Bean
    public IssueCertificateUseCase issueCertificateUseCase(
            CertificateRepository certificateRepository,
            VerificationCodeGenerator verificationCodeGenerator,
            DomainEventPublisher eventPublisher) {
        return new IssueCertificateUseCase(
                certificateRepository,
                verificationCodeGenerator,
                eventPublisher
        );
    }

    // ─── Student ────────────────────────────────────────────────

    @Bean
    public RegisterStudentUseCase registerStudentUseCase(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            DomainEventPublisher eventPublisher) {
        return new RegisterStudentUseCase(
                studentRepository,
                passwordEncoder,
                eventPublisher
        );
    }

    @Bean
    public LoginStudentUseCase loginStudentUseCase(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            TokenGenerator tokenGenerator) {
        return new LoginStudentUseCase(
                studentRepository,
                passwordEncoder,
                tokenGenerator
        );
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            DomainEventPublisher eventPublisher) {
        return new ChangePasswordUseCase(
                studentRepository,
                passwordEncoder,
                eventPublisher
        );
    }

    @Bean
    public SuspendStudentUseCase suspendStudentUseCase(
            StudentRepository studentRepository,
            DomainEventPublisher eventPublisher) {
        return new SuspendStudentUseCase(studentRepository, eventPublisher);
    }

    @Bean
    public BanStudentUseCase banStudentUseCase(
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            DomainEventPublisher eventPublisher) {
        return new BanStudentUseCase(studentRepository, enrollmentRepository, eventPublisher);
    }

    @Bean
    public ReactivateStudentUseCase reactivateStudentUseCase(
            StudentRepository studentRepository,
            DomainEventPublisher eventPublisher) {
        return new ReactivateStudentUseCase(studentRepository, eventPublisher);
    }

    @Bean
    public GetStudentUseCase getStudentUseCase(StudentRepository studentRepository) {
        return new GetStudentUseCase(studentRepository);
    }

    // ─── noOp para testes sem Kafka ─────────────────────────────

    @Bean
    @ConditionalOnMissingBean(DomainEventPublisher.class)
    public DomainEventPublisher noOpDomainEventPublisher() {
        return event -> {};
    }
}
