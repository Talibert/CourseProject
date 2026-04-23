package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.command.ChangePasswordCommand;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.PasswordEncoder;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.exception.InvalidCredentialsException;
import com.example.api_docker.domain.student.exception.StudentNotActiveException;
import com.example.api_docker.domain.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChangePasswordUseCase {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    public void execute(ChangePasswordCommand command) {
        Student student = studentRepository.findById(command.studentId())
                .orElseThrow(() -> new StudentNotFoundException(command.studentId()));

        if (!student.isActive())
            throw new StudentNotActiveException(student.getId());

        if (!passwordEncoder.matches(command.currentPassword(), student.getPasswordHash()))
            throw new InvalidCredentialsException();

        String newPasswordHash = passwordEncoder.encode(command.newPassword());

        student.changePassword(newPasswordHash);
        studentRepository.save(student);

        student.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
