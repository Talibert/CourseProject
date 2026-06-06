package com.example.api_docker.infra.persistence.enrollment;

import com.example.api_docker.domain.enrollment.EnrollmentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, UUID> {
    boolean existsActiveByStudentIdAndCourseId(UUID studentId, UUID courseId);
    List<EnrollmentJpaEntity> findByStudentIdAndStatus(UUID studentId, EnrollmentStatusType statusType);
    List<EnrollmentJpaEntity> findAllByStudentId(UUID studentId);
}
