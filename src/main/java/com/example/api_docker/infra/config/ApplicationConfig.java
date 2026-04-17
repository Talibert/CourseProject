package com.example.api_docker.infra.config;

import com.example.api_docker.application.certificate.usecase.IssueCertificateUseCase;
import com.example.api_docker.domain.certificate.CertificateRepository;
import com.example.api_docker.domain.certificate.VerificationCodeGenerator;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IMPORTANTE: estamos definindo os useCases como beans manualmente para manter o principio da clean arch.
 * A ideia é que o useCase não conheça o framework
 */
@Configuration
public class ApplicationConfig {


    /**
     * Quando o kafka estiver desabilitado via properties, injetamos um bean nulo.
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(DomainEventPublisher.class)
    public DomainEventPublisher noOpDomainEventPublisher() {
        return event -> {};
    }

    @Bean
    public IssueCertificateUseCase issueCertificateUseCase(CertificateRepository certificateRepository,
            VerificationCodeGenerator verificationCodeGenerator, DomainEventPublisher eventPublisher) {
        return new IssueCertificateUseCase(certificateRepository, verificationCodeGenerator, eventPublisher);
    }
}
