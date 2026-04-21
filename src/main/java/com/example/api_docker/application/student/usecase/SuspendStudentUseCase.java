package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.command.SuspendStudentCommand;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SuspendStudentUseCase {

    private final StudentRepository studentRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(SuspendStudentCommand command) {
        var student = studentRepository.findById(command.studentId())
                .orElseThrow(() -> new StudentNotFoundException(command.studentId()));

        student.suspend();

        studentRepository.save(student);
        student.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
