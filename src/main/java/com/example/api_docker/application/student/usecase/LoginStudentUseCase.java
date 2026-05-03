package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.command.LoginCommand;
import com.example.api_docker.application.student.result.LoginResult;
import com.example.api_docker.domain.student.*;
import com.example.api_docker.domain.student.exception.InvalidCredentialsException;
import com.example.api_docker.domain.student.exception.StudentNotActiveException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LoginStudentUseCase {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public LoginResult execute(LoginCommand command) {
        Student student = studentRepository.findByEmail(new Email(command.email()))
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!student.isActive())
            throw new StudentNotActiveException(student.getId());

        if (!passwordEncoder.matches(command.rawPassword(), student.getPasswordHash()))
            throw new InvalidCredentialsException();

        String token = tokenGenerator.generate(student.getId(), student.getEmail(), UserRole.STUDENT);
        return new LoginResult(token, student.getId().value(), student.getName().full());
    }
}
