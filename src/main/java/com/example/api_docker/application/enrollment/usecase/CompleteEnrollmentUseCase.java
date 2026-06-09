package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.application.enrollment.command.CompleteEnrollmentCommand;
import com.example.api_docker.domain.certificate.CertificatePolicy;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompleteEnrollmentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final CertificatePolicy certificatePolicy;
    private final DomainEventPublisher eventPublisher;

    public void execute(CompleteEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));

        Course course = courseRepository.findById(enrollment.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(enrollment.getCourseId()));

        enrollment.complete(certificatePolicy);
        enrollmentRepository.save(enrollment);
        enrollment.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
