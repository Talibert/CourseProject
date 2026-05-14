package com.example.api_docker.application.auth;

import com.example.api_docker.application.shared.LoginCommand;
import com.example.api_docker.application.shared.LoginResult;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.user.*;
import com.example.api_docker.domain.user.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LoginUseCase {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public LoginResult execute(LoginCommand command) {
        Email email = new Email(command.email());

        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent())
            return authenticate(student.get(), command.rawPassword(), UserRole.STUDENT);

        Optional<Instructor> instructor = instructorRepository.findByEmail(email);
        if (instructor.isPresent())
            return authenticate(instructor.get(), command.rawPassword(), UserRole.INSTRUCTOR);

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent())
            return authenticate(admin.get(), command.rawPassword(), UserRole.ADMIN);

        throw new InvalidCredentialsException();
    }

    private LoginResult authenticate(User user, String rawPassword, UserRole role) {
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash()))
            throw new InvalidCredentialsException();

        String token = tokenGenerator.generate(user.getId(), user.getEmail(), role);
        return new LoginResult(token, user.getId().value(), user.getName().full(), role.name());
    }
}
