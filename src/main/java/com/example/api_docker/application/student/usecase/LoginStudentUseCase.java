package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.command.LoginCommand;
import com.example.api_docker.application.student.result.LoginResult;
import com.example.api_docker.domain.student.Email;
import com.example.api_docker.domain.student.PasswordEncoder;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.TokenGenerator;
import com.example.api_docker.domain.student.exception.InvalidCredentialsException;
import com.example.api_docker.domain.student.exception.StudentNotActiveException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginStudentUseCase {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public LoginResult execute(LoginCommand command) {
        var student = studentRepository.findByEmail(new Email(command.email()))
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!student.isActive())
            throw new StudentNotActiveException(student.getId());

        if (!passwordEncoder.matches(command.rawPassword(), student.getPasswordHash()))
            throw new InvalidCredentialsException();

        String token = tokenGenerator.generate(student.getId(), student.getEmail());
        return new LoginResult(token, student.getId(), student.getName().full());
    }
}
