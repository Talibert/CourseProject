package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.query.ListStudentEnrollmentsQuery;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.*;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListStudentEnrollmentsUseCaseTest extends UnitAbstractTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private ListStudentEnrollmentsUseCase listStudentEnrollmentsUseCase;

    private UserId studentId;

    @BeforeEach
    void setUp() {
        studentId = new UserId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Deve retornar lista de matrículas quando student tem matrículas")
    void shouldReturnEnrollmentListWhenStudentHasEnrollments() {
        CourseId firstCourseId = new CourseId(UUID.randomUUID());
        CourseId secondCourseId = new CourseId(UUID.randomUUID());

        Enrollment firstEnrollment = Enrollment.restore(
                new EnrollmentId(UUID.randomUUID()),
                studentId,
                firstCourseId,
                EnrollmentStatusType.ACTIVE,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );

        Enrollment secondEnrollment = Enrollment.restore(
                new EnrollmentId(UUID.randomUUID()),
                studentId,
                secondCourseId,
                EnrollmentStatusType.COMPLETED,
                Progress.zero(10),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(enrollmentRepository.findAllByStudentId(studentId))
                .thenReturn(List.of(firstEnrollment, secondEnrollment));

        List<EnrollmentResult> results = listStudentEnrollmentsUseCase.execute(
                new ListStudentEnrollmentsQuery(studentId)
        );

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(firstCourseId.value(), results.get(0).courseId());
        assertEquals("ACTIVE", results.get(0).status());
        assertEquals(secondCourseId.value(), results.get(1).courseId());
        assertEquals("COMPLETED", results.get(1).status());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando student não tem matrículas")
    void shouldReturnEmptyListWhenStudentHasNoEnrollments() {
        when(enrollmentRepository.findAllByStudentId(studentId)).thenReturn(List.of());

        List<EnrollmentResult> results = listStudentEnrollmentsUseCase.execute(
                new ListStudentEnrollmentsQuery(studentId)
        );

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}