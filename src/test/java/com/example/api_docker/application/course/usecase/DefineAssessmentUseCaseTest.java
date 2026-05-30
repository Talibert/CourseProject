package com.example.api_docker.application.course.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.course.command.DefineAssessmentCommand;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.exception.CourseAlreadyPublishedException;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefineAssessmentUseCaseTest extends UnitAbstractTests {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private DefineAssessmentUseCase defineAssessmentUseCase;

    private CourseId courseId;
    private Course courseDraft;
    private Course coursePublished;
    private DefineAssessmentCommand command;

    @BeforeEach
    void setUp() {
        courseId = new CourseId(UUID.randomUUID());

        courseDraft = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.DRAFT,
                new ArrayList<>(),
                null,
                null
        );

        coursePublished = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.PUBLISHED,
                new ArrayList<>(),
                null,
                LocalDateTime.now()
        );

        command = new DefineAssessmentCommand(
                courseId,
                "Prova Final",
                new BigDecimal("6.0"),
                new BigDecimal("10.0")
        );
    }

    @Test
    @DisplayName("Deve definir assessment no curso com dados válidos")
    void shouldDefineAssessmentWithValidData() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseDraft));

        defineAssessmentUseCase.execute(command);

        assertNotNull(courseDraft.getAssessment());
        assertEquals("Prova Final", courseDraft.getAssessment().getTitle());
        assertEquals(new BigDecimal("6.0"), courseDraft.getAssessment().getMinimumGrade());
        assertEquals(new BigDecimal("10.0"), courseDraft.getAssessment().getMaximumGrade());

        verify(courseRepository, times(1)).save(courseDraft);
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> defineAssessmentUseCase.execute(command)
        );

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso já está publicado")
    void shouldThrowExceptionWhenCourseAlreadyPublished() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(coursePublished));

        assertThrows(
                CourseAlreadyPublishedException.class,
                () -> defineAssessmentUseCase.execute(command)
        );

        verify(courseRepository, never()).save(any(Course.class));
    }
}