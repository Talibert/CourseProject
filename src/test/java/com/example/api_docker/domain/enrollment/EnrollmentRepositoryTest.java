package com.example.api_docker.domain.enrollment;

import com.example.api_docker.RepositoryAbstractTests;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private UserId studentId;
    private CourseId courseId;

    @BeforeEach
    void setUp() {
        studentId = new UserId(UUID.randomUUID());
        courseId = new CourseId(UUID.randomUUID());
    }

    private Enrollment buildEnrollment(EnrollmentStatusType status) {
        return Enrollment.restore(
                new EnrollmentId(UUID.randomUUID()),
                studentId,
                courseId,
                status,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve salvar e encontrar matrícula pelo id")
    void shouldSaveAndFindEnrollmentById() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.PENDING);
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> found = enrollmentRepository.findById(enrollment.getId());

        assertTrue(found.isPresent());
        assertEquals(enrollment.getId(), found.get().getId());
        assertEquals(studentId, found.get().getUserId());
        assertEquals(courseId, found.get().getCourseId());
        assertEquals(EnrollmentStatusType.PENDING, found.get().getStatus());
    }

    @Test
    @DisplayName("Deve retornar vazio quando matrícula não encontrada pelo id")
    void shouldReturnEmptyWhenEnrollmentNotFoundById() {
        Optional<Enrollment> found = enrollmentRepository.findById(
                new EnrollmentId(UUID.randomUUID())
        );

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar matrículas ativas do student")
    void shouldReturnActiveEnrollmentsByStudent() {
        Enrollment activeEnrollment = buildEnrollment(EnrollmentStatusType.ACTIVE);
        Enrollment pendingEnrollment = buildEnrollment(EnrollmentStatusType.PENDING);
        Enrollment cancelledEnrollment = buildEnrollment(EnrollmentStatusType.CANCELLED);

        enrollmentRepository.save(activeEnrollment);
        enrollmentRepository.save(pendingEnrollment);
        enrollmentRepository.save(cancelledEnrollment);

        List<Enrollment> activeEnrollments = enrollmentRepository.findActiveByStudentId(studentId);

        assertEquals(1, activeEnrollments.size());
        assertEquals(EnrollmentStatusType.ACTIVE, activeEnrollments.getFirst().getStatus());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando student não tem matrículas ativas")
    void shouldReturnEmptyListWhenStudentHasNoActiveEnrollments() {
        List<Enrollment> activeEnrollments = enrollmentRepository.findActiveByStudentId(
                new UserId(UUID.randomUUID())
        );

        assertTrue(activeEnrollments.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar todas as matrículas do student")
    void shouldReturnAllEnrollmentsByStudent() {
        Enrollment firstEnrollment = buildEnrollment(EnrollmentStatusType.ACTIVE);
        Enrollment secondEnrollment = Enrollment.restore(
                new EnrollmentId(UUID.randomUUID()),
                studentId,
                new CourseId(UUID.randomUUID()),
                EnrollmentStatusType.COMPLETED,
                Progress.zero(10),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        enrollmentRepository.save(firstEnrollment);
        enrollmentRepository.save(secondEnrollment);

        List<Enrollment> allEnrollments = enrollmentRepository.findAllByStudentId(studentId);

        assertEquals(2, allEnrollments.size());
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando existe matrícula ativa para student e curso")
    void shouldReturnTrueWhenActiveEnrollmentExistsForStudentAndCourse() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.ACTIVE);
        enrollmentRepository.save(enrollment);

        boolean exists = enrollmentRepository.existsActiveByStudentAndCourse(
                studentId.value(), courseId.value()
        );

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar falso quando não existe matrícula ativa para student e curso")
    void shouldReturnFalseWhenNoActiveEnrollmentExistsForStudentAndCourse() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.CANCELLED);
        enrollmentRepository.save(enrollment);

        boolean exists = enrollmentRepository.existsActiveByStudentAndCourse(
                studentId.value(), courseId.value()
        );

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve atualizar matrícula ao salvar com mesmo id")
    void shouldUpdateEnrollmentWhenSavingWithSameId() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.PENDING);
        enrollmentRepository.save(enrollment);

        Enrollment updated = Enrollment.restore(
                enrollment.getId(),
                studentId,
                courseId,
                EnrollmentStatusType.ACTIVE,
                Progress.zero(10),
                enrollment.getEnrolledAt(),
                null
        );
        enrollmentRepository.save(updated);

        Optional<Enrollment> found = enrollmentRepository.findById(enrollment.getId());

        assertTrue(found.isPresent());
        assertEquals(EnrollmentStatusType.ACTIVE, found.get().getStatus());
    }
}