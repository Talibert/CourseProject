package com.example.api_docker.application.admin.usecase;

import com.example.api_docker.application.shared.LoginCommand;
import com.example.api_docker.application.shared.LoginResult;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.PasswordEncoder;
import com.example.api_docker.domain.user.TokenGenerator;
import com.example.api_docker.domain.user.UserRole;
import com.example.api_docker.domain.user.exception.InvalidCredentialsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoginAdminUseCase {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public LoginResult execute(LoginCommand command) {
        Admin admin = adminRepository.findByEmail(new Email(command.email()))
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(command.rawPassword(), admin.getPasswordHash()))
            throw new InvalidCredentialsException();

        String token = tokenGenerator.generate(
                admin.getId(),
                admin.getEmail(),
                UserRole.ADMIN
        );

        return new LoginResult(token, admin.getId().value(), admin.getName().full());
    }
}
