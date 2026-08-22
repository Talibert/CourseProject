package com.example.api_docker.domain.certificate;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.course.AssessmentId;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.LessonId;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.enrollment.EnrollmentStatusType;
import com.example.api_docker.domain.enrollment.Progress;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardCertificatePolicyTest extends UnitAbstractTests {

    private StandardCertificatePolicy policy;
    private UserId userId;
    private CourseId courseId;
    private AssessmentId assessmentId;

    @BeforeEach
    void setUp() {
        policy = new StandardCertificatePolicy();
        userId = UserId.generate();
        courseId = new CourseId(UUID.randomUUID());
        assessmentId = AssessmentId.generate();
    }

    private Enrollment createEnrollmentWithProgress(int completedCount, int totalLessons, BigDecimal grade) {
        Set<LessonId> lessons = new HashSet<>();
        for (int i = 0; i < completedCount; i++) {
            lessons.add(LessonId.generate());
        }

        Map<AssessmentId, BigDecimal> grades = grade != null ? Map.of(assessmentId, grade) : Map.of();
        Progress progress = new Progress(Set.copyOf(lessons), grades, totalLessons);

        return Enrollment.restore(
                EnrollmentId.generate(),
                userId,
                courseId,
                EnrollmentStatusType.ACTIVE,
                progress,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve retornar true quando progresso for maior ou igual a 70% e nota for maior ou igual a 6.0")
    void shouldReturnTrueWhenProgressAndGradeSatisfyPolicy() {
        // 7 a 10 aulas concluídas (70%) e nota 6.0
        Enrollment enrollment = createEnrollmentWithProgress(7, 10, new BigDecimal("6.0"));

        assertTrue(policy.isSatisfiedBy(enrollment));
    }

    @Test
    @DisplayName("Deve retornar false quando progresso for menor que 70%")
    void shouldReturnFalseWhenProgressIsBelowMinimum() {
        // 6 a 10 aulas concluídas (60%) e nota 10.0
        Enrollment enrollment = createEnrollmentWithProgress(6, 10, new BigDecimal("10.0"));

        assertFalse(policy.isSatisfiedBy(enrollment));
    }

    @Test
    @DisplayName("Deve retornar false quando a nota for menor que 6.0")
    void shouldReturnFalseWhenGradeIsBelowMinimum() {
        // 10 a 10 aulas concluídas (100%) e nota 5.9
        Enrollment enrollment = createEnrollmentWithProgress(10, 10, new BigDecimal("5.9"));

        assertFalse(policy.isSatisfiedBy(enrollment));
    }

    @Test
    @DisplayName("Deve retornar false quando não houver notas registradas")
    void shouldReturnFalseWhenNoGradesRecorded() {
        // 10 a 10 aulas concluídas (100%) mas sem nota
        Enrollment enrollment = createEnrollmentWithProgress(10, 10, null);

        assertFalse(policy.isSatisfiedBy(enrollment));
    }
}
