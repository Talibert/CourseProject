package com.example.api_docker.application.course.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.course.command.PublishCourseCommand;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.Module;
import com.example.api_docker.domain.course.event.CoursePublishedEvent;
import com.example.api_docker.domain.course.exception.CourseAlreadyPublishedException;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.course.exception.CoursePublishNotAllowedException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishCourseUseCaseTest extends UnitAbstractTests {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private PublishCourseUseCase publishCourseUseCase;

    private CourseId courseId;
    private Course courseReadyToPublish;
    private Course courseWithoutModules;
    private Course courseWithoutAssessment;
    private Course courseAlreadyPublished;

    @BeforeEach
    void setUp() {
        courseId = new CourseId(UUID.randomUUID());

        Module module = Module.restore(
                new ModuleId(UUID.randomUUID()),
                "Fundamentos",
                1,
                List.of(Lesson.create("Introdução", 1, 30))
        );

        Assessment assessment = new Assessment(
                new AssessmentId(UUID.randomUUID()),
                "Prova Final",
                new BigDecimal("6.0"),
                new BigDecimal("10.0")
        );

        courseReadyToPublish = Course.restore(
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

        courseWithoutModules = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.DRAFT,
                new ArrayList<>(),
                assessment,
                null
        );

        courseWithoutAssessment = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.DRAFT,
                new ArrayList<>(List.of(module)),
                null,
                null
        );

        courseAlreadyPublished = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.PUBLISHED,
                List.of(module),
                assessment,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve publicar curso com dados válidos")
    void shouldPublishCourseWithValidData() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseReadyToPublish));

        publishCourseUseCase.execute(new PublishCourseCommand(courseId));

        assertEquals(CourseStatusType.PUBLISHED, courseReadyToPublish.getStatus());

        verify(courseRepository, times(1)).save(courseReadyToPublish);

        verify(eventPublisher, times(1)).publish(any(CoursePublishedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> publishCourseUseCase.execute(new PublishCourseCommand(courseId))
        );

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso já está publicado")
    void shouldThrowExceptionWhenCourseAlreadyPublished() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseAlreadyPublished));

        assertThrows(
                CourseAlreadyPublishedException.class,
                () -> publishCourseUseCase.execute(new PublishCourseCommand(courseId))
        );

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não tem módulos")
    void shouldThrowExceptionWhenCourseHasNoModules() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseWithoutModules));

        assertThrows(
                CoursePublishNotAllowedException.class,
                () -> publishCourseUseCase.execute(new PublishCourseCommand(courseId))
        );

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não tem assessment")
    void shouldThrowExceptionWhenCourseHasNoAssessment() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseWithoutAssessment));

        assertThrows(
                CoursePublishNotAllowedException.class,
                () -> publishCourseUseCase.execute(new PublishCourseCommand(courseId))
        );

        verify(courseRepository, never()).save(any(Course.class));
    }
}