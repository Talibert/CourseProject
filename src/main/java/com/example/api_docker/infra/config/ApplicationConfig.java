package com.example.api_docker.infra.config;

import com.example.api_docker.application.certificate.IssueCertificateUseCase;
import com.example.api_docker.domain.certificate.CertificateRepository;
import com.example.api_docker.domain.certificate.VerificationCodeGenerator;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IMPORTANTE: estamos definindo os useCases como beans manualmente para manter o principio da clean arch.
 * A ideia é que o useCase não conheça o framework
 */
@Configuration
public class ApplicationConfig {


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
}
