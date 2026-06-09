package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.query.GetEnrollmentQuery;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.*;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEnrollmentUseCaseTest extends UnitAbstractTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private GetEnrollmentUseCase getEnrollmentUseCase;

    private EnrollmentId enrollmentId;
    private UserId studentId;
    private CourseId courseId;
    private LocalDateTime enrolledAt;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        studentId = new UserId(UUID.randomUUID());
        courseId = new CourseId(UUID.randomUUID());
        enrolledAt = LocalDateTime.now();
    }

    @Test
    @DisplayName("Deve retornar matrícula quando encontrada pelo id")
    void shouldReturnEnrollmentWhenFoundById() {
        Enrollment enrollment = Enrollment.restore(
                enrollmentId,
                studentId,
                courseId,
                EnrollmentStatusType.ACTIVE,
                Progress.zero(10),
                enrolledAt,
                null
        );

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        EnrollmentResult result = getEnrollmentUseCase.execute(new GetEnrollmentQuery(enrollmentId));

        assertNotNull(result);
        assertEquals(enrollmentId.value(), result.enrollmentId());
        assertEquals(studentId.value(), result.studentId());
        assertEquals(courseId.value(), result.courseId());
        assertEquals("ACTIVE", result.status());
        assertEquals(0.0, result.progressPercentage());
        assertNull(result.completedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não encontrada")
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> getEnrollmentUseCase.execute(new GetEnrollmentQuery(enrollmentId))
        );
    }
}