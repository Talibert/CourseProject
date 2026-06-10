package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.command.WatchLessonCommand;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.Module;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.course.exception.LessonNotPartOfCourseException;
import com.example.api_docker.domain.enrollment.*;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotActiveException;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchLessonUseCaseTest extends UnitAbstractTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private WatchLessonUseCase watchLessonUseCase;

    private EnrollmentId enrollmentId;
    private UserId studentId;
    private CourseId courseId;
    private LessonId lessonId;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        studentId = new UserId(UUID.randomUUID());
        courseId = new CourseId(UUID.randomUUID());
        lessonId = new LessonId(UUID.randomUUID());

        Lesson lesson = Lesson.restore(lessonId, "Introdução", 1, 30);

        Module module = Module.restore(
                new ModuleId(UUID.randomUUID()),
                "Fundamentos",
                1,
                List.of(lesson)
        );

        course = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.PUBLISHED,
                new ArrayList<>(List.of(module)),
                new Assessment(
                        new AssessmentId(UUID.randomUUID()),
                        "Prova Final",
                        new BigDecimal("6.0"),
                        new BigDecimal("10.0")
                ),
                LocalDateTime.now()
        );
    }

    private Enrollment buildEnrollment(EnrollmentStatusType status) {
        return Enrollment.restore(
                enrollmentId,
                studentId,
                courseId,
                status,
                Progress.zero(1),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve registrar progresso de aula com sucesso")
    void shouldRecordLessonProgressSuccessfully() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.ACTIVE);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        watchLessonUseCase.execute(new WatchLessonCommand(enrollmentId, lessonId));

        assertTrue(enrollment.getProgress().completedLessons().contains(lessonId));
        verify(enrollmentRepository, times(1)).save(enrollment);
        verify(eventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não encontrada")
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> watchLessonUseCase.execute(new WatchLessonCommand(enrollmentId, lessonId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.ACTIVE);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> watchLessonUseCase.execute(new WatchLessonCommand(enrollmentId, lessonId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não está ativa")
    void shouldThrowExceptionWhenEnrollmentIsNotActive() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.PENDING);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(
                EnrollmentNotActiveException.class,
                () -> watchLessonUseCase.execute(new WatchLessonCommand(enrollmentId, lessonId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando aula não pertence ao curso")
    void shouldThrowExceptionWhenLessonDoesNotBelongToCourse() {
        LessonId inexistentLessonId = new LessonId(UUID.randomUUID());
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.ACTIVE);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(
                LessonNotPartOfCourseException.class,
                () -> watchLessonUseCase.execute(new WatchLessonCommand(enrollmentId, inexistentLessonId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }
}