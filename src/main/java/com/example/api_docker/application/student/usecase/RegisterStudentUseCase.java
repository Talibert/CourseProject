package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.command.RegisterStudentCommand;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.*;
import com.example.api_docker.domain.student.exception.CpfAlreadyInUseException;
import com.example.api_docker.domain.student.exception.EmailAlreadyInUseException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RegisterStudentUseCase {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    public void execute(RegisterStudentCommand command) {
        if (studentRepository.existsByEmail(new Email(command.email())))
            throw new EmailAlreadyInUseException(command.email());

        if (studentRepository.existsByCpf(new Cpf(command.cpf())))
            throw new CpfAlreadyInUseException(command.cpf());


        String passwordHash = passwordEncoder.encode(command.rawPassword());

        Student student = Student.create(
                new FullName(command.firstName(), command.lastName()),
                new Email(command.email()),
                new Cpf(command.cpf()),
                command.birthDate(),
                passwordHash
        );

        studentRepository.save(student);
        student.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
