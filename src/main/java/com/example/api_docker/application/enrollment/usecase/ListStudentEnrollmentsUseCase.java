package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.application.enrollment.query.ListStudentEnrollmentsQuery;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListStudentEnrollmentsUseCase {

    private final EnrollmentRepository enrollmentRepository;

    public List<EnrollmentResult> execute(ListStudentEnrollmentsQuery query) {
        return enrollmentRepository.findAllByStudentId(query.studentId()).stream()
                .map(EnrollmentResult::from)
                .toList();
    }
}
