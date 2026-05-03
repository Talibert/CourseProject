package com.example.api_docker.infra.config;

import com.example.api_docker.application.admin.command.CreateAdminCommand;
import com.example.api_docker.application.admin.usecase.CreateAdminUseCase;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.user.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "admin.seed.enabled", havingValue = "true", matchIfMissing = true)
public class AdminSeedConfig {

    @Value("${admin.seed.firstName}")
    private String firstName;

    @Value("${admin.seed.lastName}")
    private String lastName;

    @Value("${admin.seed.email}")
    private String email;

    @Value("${admin.seed.password}")
    private String password;

    @Bean
    public CommandLineRunner seedAdmin(CreateAdminUseCase createAdminUseCase,
                                       AdminRepository adminRepository) {
        return args -> {
            var adminEmail = new Email(email);
            if (!adminRepository.existsByEmail(adminEmail)) {
                createAdminUseCase.execute(new CreateAdminCommand(firstName, lastName, email, password));

                log.info("Admin inicial criado: {}", email);
            } else {
                log.warn("Admin inicial já existe, seed ignorado.");
            }
        };
    }
}
