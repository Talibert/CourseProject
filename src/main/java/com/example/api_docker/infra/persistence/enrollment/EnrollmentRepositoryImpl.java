package com.example.api_docker.infra.persistence.enrollment;

import com.example.api_docker.domain.course.AssessmentId;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.LessonId;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.enrollment.Progress;
import com.example.api_docker.domain.student.StudentId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Component
public class EnrollmentRepositoryImpl implements EnrollmentRepository {
    private final EnrollmentJpaRepository jpaRepository;

    public EnrollmentRepositoryImpl(EnrollmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Enrollment enrollment) {
        EnrollmentJpaEntity jpaEntity = toJpaEntity(enrollment);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public Optional<Enrollment> findById(EnrollmentId id) {
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<Enrollment> findActiveByStudentAndCourse(StudentId studentId, CourseId courseId) {
        // TODO implementar
        return Optional.empty();
    }

    private EnrollmentJpaEntity toJpaEntity(Enrollment enrollment) {
        EnrollmentJpaEntity entity = new EnrollmentJpaEntity();
        entity.setId(enrollment.getId().value());
        entity.setStudentId(enrollment.getStudentId().value());
        entity.setCourseId(enrollment.getCourseId().value());
        entity.setStatus(enrollment.getStatus());
        entity.setEnrolledAt(enrollment.getEnrolledAt());
        entity.setCompletedAt(enrollment.getCompletedAt());

        Progress progress = enrollment.getProgress();
        entity.setTotalLessons(progress.totalLessons());
        entity.setCompletedLessons(
                progress.completedLessons().stream()
                        .map(LessonId::value)
                        .collect(toSet())
        );

        entity.setGrades(
                progress.grades().entrySet().stream()
                        .collect(toMap(e -> e.getKey().value(), Map.Entry::getValue))
        );

        return entity;
    }

    private Enrollment toDomain(EnrollmentJpaEntity entity) {
        Set<LessonId> completedLessons = entity.getCompletedLessons().stream()
                .map(LessonId::new).collect(toSet());

        Map<AssessmentId, BigDecimal> grades = entity.getGrades().entrySet().stream()
                .collect(toMap(e -> new AssessmentId(e.getKey()), Map.Entry::getValue));

        Progress progress = new Progress(completedLessons, grades, entity.getTotalLessons());

        return Enrollment.restore(new EnrollmentId(entity.getId()), new StudentId(entity.getStudentId()),
                new CourseId(entity.getCourseId()), entity.getStatus(), progress, entity.getEnrolledAt(),
                entity.getCompletedAt());
    }
}
