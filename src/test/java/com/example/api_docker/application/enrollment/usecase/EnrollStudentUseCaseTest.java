package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.command.EnrollStudentCommand;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.Module;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.course.exception.CourseNotPublishedException;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.enrollment.event.EnrollmentCreatedEvent;
import com.example.api_docker.domain.enrollment.exception.EnrollmentAlreadyExistsException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollStudentUseCaseTest extends UnitAbstractTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private EnrollStudentUseCase enrollStudentUseCase;

    private UserId studentId;
    private CourseId courseId;
    private Course publishedCourse;
    private Course draftCourse;
    private EnrollStudentCommand command;

    @BeforeEach
    void setUp() {
        studentId = new UserId(UUID.randomUUID());
        courseId = new CourseId(UUID.randomUUID());

        Module module = Module.restore(
                new ModuleId(UUID.randomUUID()),
                "Fundamentos",
                1,
                List.of(new Lesson("Introdução", 1, 30))
        );

        Assessment assessment = new Assessment(
                new AssessmentId(UUID.randomUUID()),
                "Prova Final",
                new BigDecimal("6.0"),
                new BigDecimal("10.0")
        );

        publishedCourse = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.PUBLISHED,
                new ArrayList<>(List.of(module)),
                assessment,
                LocalDateTime.now()
        );

        draftCourse = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.DRAFT,
                new ArrayList<>(List.of(module)),
                assessment,
                null
        );

        command = new EnrollStudentCommand(studentId, courseId);
    }

    @Test
    @DisplayName("Deve matricular student em curso publicado com sucesso")
    void shouldEnrollStudentInPublishedCourseSuccessfully() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(publishedCourse));
        when(enrollmentRepository.existsActiveByStudentAndCourse(
                studentId.value(), courseId.value())).thenReturn(false);

        EnrollmentResult result = enrollStudentUseCase.execute(command);

        assertEquals("PENDING", result.status());
        assertEquals(studentId.value(), result.studentId());
        assertEquals(courseId.value(), result.courseId());
        assertNotNull(result.enrollmentId());
        assertEquals(0.0, result.progressPercentage());
        assertNull(result.completedAt());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
        verify(eventPublisher, times(1)).publish(any(EnrollmentCreatedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> enrollStudentUseCase.execute(command)
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não está publicado")
    void shouldThrowExceptionWhenCourseIsNotPublished() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(draftCourse));

        assertThrows(
                CourseNotPublishedException.class,
                () -> enrollStudentUseCase.execute(command)
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula já existe")
    void shouldThrowExceptionWhenEnrollmentAlreadyExists() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(publishedCourse));
        when(enrollmentRepository.existsActiveByStudentAndCourse(
                studentId.value(), courseId.value())).thenReturn(true);

        assertThrows(
                EnrollmentAlreadyExistsException.class,
                () -> enrollStudentUseCase.execute(command)
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }
}