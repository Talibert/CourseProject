package com.example.api_docker.application.course.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.course.query.GetCourseQuery;
import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCourseUseCaseTest extends UnitAbstractTests {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private GetCourseUseCase getCourseUseCase;

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
    @DisplayName("Deve retornar curso quando encontrado pelo id")
    void shouldReturnCourseWhenFoundById() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        CourseResult result = getCourseUseCase.execute(new GetCourseQuery(courseId));

        assertNotNull(result);
        assertEquals(courseId.value(), result.courseId());
        assertEquals("Clean Architecture na Prática", result.title());
        assertEquals("Aprenda Clean Architecture com Java 21", result.description());
        assertEquals(new BigDecimal("199.90"), result.price());
        assertEquals("BRL", result.currency());
        assertEquals(20, result.estimatedHours());
        assertEquals("DRAFT", result.status());
        assertTrue(result.modules().isEmpty());
        assertNull(result.assessment());
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> getCourseUseCase.execute(new GetCourseQuery(courseId))
        );
    }
}