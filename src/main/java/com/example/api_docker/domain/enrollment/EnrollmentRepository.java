package com.example.api_docker.domain.enrollment;

import com.example.api_docker.domain.user.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Optional<Enrollment> findById(EnrollmentId id);
    List<Enrollment> findActiveByStudentId(UserId studentId);
    List<Enrollment> findAllByStudentId(UserId studentId);
    boolean existsActiveByStudentAndCourse(UUID userId, UUID courseId);
}
