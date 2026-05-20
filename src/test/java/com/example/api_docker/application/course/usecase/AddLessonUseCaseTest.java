package com.example.api_docker.application.course.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.course.command.AddLessonCommand;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.Module;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.course.exception.ModuleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddLessonUseCaseTest extends UnitAbstractTests {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AddLessonUseCase addLessonUseCase;

    private CourseId courseId;
    private ModuleId moduleId;
    private Course course;

    @BeforeEach
    void setUp() {
        courseId = new CourseId(UUID.randomUUID());
        moduleId = new ModuleId(UUID.randomUUID());

        Module module = Module.restore(
                moduleId,
                "Fundamentos",
                1,
                new ArrayList<>()
        );

        course = Course.restore(
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
    }

    @Test
    @DisplayName("Deve adicionar aula ao módulo com dados válidos")
    void shouldAddLessonToModuleWithValidData() {
        AddLessonCommand command = new AddLessonCommand(
                courseId, moduleId, "Introdução à Clean Architecture", 1, 30
        );

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        addLessonUseCase.execute(command);

        Module module = course.getModules().stream()
                .filter(m -> m.getId().equals(moduleId))
                .findFirst()
                .orElseThrow();

        assertEquals(1, module.getLessons().size());
        assertEquals("Introdução à Clean Architecture", module.getLessons().getFirst().getTitle());

        verify(courseRepository, times(1)).save(course);
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        AddLessonCommand command = new AddLessonCommand(
                courseId, moduleId, "Introdução à Clean Architecture", 1, 30
        );

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> addLessonUseCase.execute(command)
        );

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando módulo não encontrado")
    void shouldThrowExceptionWhenModuleNotFound() {
        ModuleId inexistentModuleId = new ModuleId(UUID.randomUUID());
        AddLessonCommand command = new AddLessonCommand(
                courseId, inexistentModuleId, "Introdução à Clean Architecture", 1, 30
        );

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(
                ModuleNotFoundException.class,
                () -> addLessonUseCase.execute(command)
        );

        verify(courseRepository, never()).save(any(Course.class));
    }
}