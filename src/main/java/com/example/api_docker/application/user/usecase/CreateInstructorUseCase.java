package com.example.api_docker.application.user.usecase;

import com.example.api_docker.application.user.command.CreateInstructorCommand;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.PasswordEncoder;
import com.example.api_docker.domain.user.UserRepository;
import com.example.api_docker.domain.user.exception.EmailAlreadyInUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreateInstructorUseCase {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    public void execute(CreateInstructorCommand command) {
        if (userRepository.existsByEmail(new Email(command.email()))) {
            throw new EmailAlreadyInUseException(command.email());
        }

        Instructor instructor = Instructor.create(
                new FullName(command.firstName(), command.lastName()),
                new Email(command.email()),
                passwordEncoder.encode(command.rawPassword()),
                command.bio(),
                command.specialty()
        );

        instructorRepository.save(instructor);
        instructor.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
