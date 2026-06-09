package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.application.enrollment.command.ReactivateEnrollmentCommand;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// application/enrollment/ReactivateEnrollmentUseCase.java
@Component
@RequiredArgsConstructor
public class ReactivateEnrollmentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(ReactivateEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));

        enrollment.reactivate();
        enrollmentRepository.save(enrollment);
        enrollment.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
