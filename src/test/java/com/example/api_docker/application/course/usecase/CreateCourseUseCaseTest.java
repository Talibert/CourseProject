package com.example.api_docker.application.course.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.course.command.CreateCourseCommand;
import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.CurrencyType;
import com.example.api_docker.domain.course.event.CourseCreatedEvent;
import com.example.api_docker.domain.course.exception.InstructorNotFoundException;
import com.example.api_docker.domain.instructor.InstructorRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCourseUseCaseTest extends UnitAbstractTests {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private CreateCourseUseCase createCourseUseCase;

    private UUID instructorId;
    private CreateCourseCommand command;

    @BeforeEach
    void setUp() {
        instructorId = UUID.randomUUID();
        command = new CreateCourseCommand(
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                instructorId,
                new BigDecimal("199.90"),
                CurrencyType.BRL,
                20
        );
    }

    @Test
    @DisplayName("Deve criar curso com dados válidos")
    void shouldCreateCourseWithValidData() {
        when(instructorRepository.existsById(new UserId(instructorId))).thenReturn(true);

        CourseResult result = createCourseUseCase.execute(command);

        assertNotNull(result);
        assertEquals("Clean Architecture na Prática", result.title());
        assertEquals("DRAFT", result.status());

        assertNotNull(result.courseId());
        assertEquals("Clean Architecture na Prática", result.title());
        assertEquals("Aprenda Clean Architecture com Java 21", result.description());
        assertEquals(new BigDecimal("199.90"), result.price());
        assertEquals("BRL", result.currency());
        assertEquals(20, result.estimatedHours());
        assertEquals("DRAFT", result.status());
        assertTrue(result.modules().isEmpty());
        assertNull(result.assessment());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando instrutor não encontrado")
    void shouldThrowExceptionWhenInstructorNotFound() {
        when(instructorRepository.existsById(new UserId(instructorId))).thenReturn(false);

        assertThrows(
                InstructorNotFoundException.class,
                () -> createCourseUseCase.execute(command)
        );

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve publicar evento após criar o curso")
    void shouldPublishEventAfterCreatingCourse() {
        when(instructorRepository.existsById(new UserId(instructorId))).thenReturn(true);

        createCourseUseCase.execute(command);

        verify(eventPublisher, times(1)).publish(any(CourseCreatedEvent.class));
    }
}