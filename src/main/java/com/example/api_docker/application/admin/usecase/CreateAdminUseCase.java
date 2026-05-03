package com.example.api_docker.application.admin.usecase;

import com.example.api_docker.application.admin.command.CreateAdminCommand;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.PasswordEncoder;
import com.example.api_docker.domain.user.exception.EmailAlreadyInUseException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateAdminUseCase {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    public void execute(CreateAdminCommand command) {
        if (adminRepository.existsByEmail(new Email(command.email())))
            throw new EmailAlreadyInUseException(command.email());

        Admin admin = Admin.create(
                new FullName(command.firstName(), command.lastName()),
                new Email(command.email()),
                passwordEncoder.encode(command.rawPassword())
        );

        adminRepository.save(admin);
        admin.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
