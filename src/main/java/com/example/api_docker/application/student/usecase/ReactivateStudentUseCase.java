package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.command.ReactivateStudentCommand;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReactivateStudentUseCase {

    private final StudentRepository studentRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(ReactivateStudentCommand command) {
        var student = studentRepository.findById(command.userId())
                .orElseThrow(() -> new StudentNotFoundException(command.userId()));

        // Domínio valida — só SUSPENDED pode reativar, BANNED não
        student.reactivate();

        studentRepository.save(student);
        student.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
