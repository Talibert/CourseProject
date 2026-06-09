package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.application.enrollment.query.GetEnrollmentQuery;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetEnrollmentUseCase {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentResult execute(GetEnrollmentQuery query) {
        Enrollment enrollment = enrollmentRepository.findById(query.enrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException(query.enrollmentId()));

        return EnrollmentResult.from(enrollment);
    }
}
