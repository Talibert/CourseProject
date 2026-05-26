package com.example.api_docker.application.course.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.course.command.AddModuleCommand;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.course.exception.DuplicateModuleOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddModuleUseCaseTest extends UnitAbstractTests {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AddModuleUseCase addModuleUseCase;

    private CourseId courseId;
    private Course course;

    @BeforeEach
    void setUp() {
        courseId = new CourseId(UUID.randomUUID());
        course = Course.restore(
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
    }

    @Test
    @DisplayName("Deve adicionar módulo ao curso com dados válidos")
    void shouldAddModuleToCourseWithValidData() {
        AddModuleCommand command = new AddModuleCommand(courseId, "Fundamentos", 1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        addModuleUseCase.execute(command);

        assertEquals(1, course.getModules().size());
        assertEquals("Fundamentos", course.getModules().getFirst().getTitle());

        verify(courseRepository, times(1)).save(course);
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        AddModuleCommand command = new AddModuleCommand(courseId, "Fundamentos", 1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> addModuleUseCase.execute(command)
        );

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem do módulo já existe")
    void shouldThrowExceptionWhenModuleOrderAlreadyExists() {
        AddModuleCommand firstCommand = new AddModuleCommand(courseId, "Fundamentos", 1);
        AddModuleCommand duplicateCommand = new AddModuleCommand(courseId, "Avançado", 1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        addModuleUseCase.execute(firstCommand);

        assertThrows(
                DuplicateModuleOrderException.class,
                () -> addModuleUseCase.execute(duplicateCommand)
        );

        verify(courseRepository, times(1)).save(course);
    }
}