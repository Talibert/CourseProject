package com.example.api_docker.domain.enrollment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Optional<Enrollment> findById(EnrollmentId id);
    List<Enrollment> findActiveByStudentId(UUID userId);
    List<Enrollment> findAllByStudentId(UUID studentId);
    boolean existsActiveByStudentAndCourse(UUID userId, UUID courseId);
}
