package com.example.api_docker.application.course.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.domain.course.*;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCoursesUseCaseTest extends UnitAbstractTests {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ListCoursesUseCase listCoursesUseCase;

    private Course firstCourse;
    private Course secondCourse;

    @BeforeEach
    void setUp() {
        firstCourse = Course.restore(
                new CourseId(UUID.randomUUID()),
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

        secondCourse = Course.restore(
                new CourseId(UUID.randomUUID()),
                "DDD com Java",
                "Domain Driven Design na prática",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("149.90"), CurrencyType.BRL),
                15,
                CourseStatusType.PUBLISHED,
                new ArrayList<>(),
                null,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve retornar lista de cursos quando existem cursos")
    void shouldReturnListOfCoursesWhenCoursesExist() {
        when(courseRepository.findAll()).thenReturn(List.of(firstCourse, secondCourse));

        List<CourseResult> results = listCoursesUseCase.execute();

        assertNotNull(results);
        assertEquals(2, results.size());

        CourseResult firstResult = results.getFirst();
        assertEquals("Clean Architecture na Prática", firstResult.title());
        assertEquals("Aprenda Clean Architecture com Java 21", firstResult.description());
        assertEquals(new BigDecimal("199.90"), firstResult.price());
        assertEquals("BRL", firstResult.currency());
        assertEquals(20, firstResult.estimatedHours());
        assertEquals("DRAFT", firstResult.status());

        CourseResult secondResult = results.get(1);
        assertEquals("DDD com Java", secondResult.title());
        assertEquals("Domain Driven Design na prática", secondResult.description());
        assertEquals(new BigDecimal("149.90"), secondResult.price());
        assertEquals("BRL", secondResult.currency());
        assertEquals(15, secondResult.estimatedHours());
        assertEquals("PUBLISHED", secondResult.status());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existem cursos")
    void shouldReturnEmptyListWhenNoCoursesExist() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseResult> results = listCoursesUseCase.execute();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}