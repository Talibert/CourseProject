package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.application.enrollment.command.SuspendEnrollmentCommand;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuspendEnrollmentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(SuspendEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));

        enrollment.suspend(command.reason());
        enrollmentRepository.save(enrollment);
        enrollment.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
