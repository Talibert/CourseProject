package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.command.BanStudentCommand;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BanStudentUseCase {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(BanStudentCommand command) {
        var student = studentRepository.findById(command.studentId())
                .orElseThrow(() -> new StudentNotFoundException(command.studentId()));

        student.ban();
        studentRepository.save(student);

        enrollmentRepository.findActiveByStudent(command.studentId())
                .forEach(enrollment -> {
                    enrollment.cancel(CancellationReason.STUDENT_BANNED);
                    enrollmentRepository.save(enrollment);
                    enrollment.pullDomainEvents().forEach(eventPublisher::publish);
                });

        student.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
