package com.example.api_docker.domain.course;

import com.example.api_docker.RepositoryAbstractTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private CourseRepository courseRepository;

    private InstructorId instructorId;

    @BeforeEach
    void setUp() {
        instructorId = new InstructorId(UUID.randomUUID());
    }

    private Course buildDraftCourse(String title) {
        return Course.restore(
                new CourseId(UUID.randomUUID()),
                title,
                "Descrição do curso",
                instructorId,
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.DRAFT,
                new ArrayList<>(),
                null,
                null
        );
    }

    private Course buildPublishedCourse(String title) {
        Module module = Module.restore(
                new ModuleId(UUID.randomUUID()),
                "Fundamentos",
                1,
                List.of(Lesson.restore(
                        new LessonId(UUID.randomUUID()),
                        "Introdução",
                        1,
                        30
                ))
        );

        Assessment assessment = new Assessment(
                new AssessmentId(UUID.randomUUID()),
                "Prova Final",
                new BigDecimal("6.00"),
                new BigDecimal("10.00")
        );

        return Course.restore(
                new CourseId(UUID.randomUUID()),
                title,
                "Descrição do curso",
                instructorId,
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.PUBLISHED,
                new ArrayList<>(List.of(module)),
                assessment,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve salvar e encontrar curso pelo id")
    void shouldSaveAndFindCourseById() {
        Course course = buildDraftCourse("Clean Architecture na Prática");
        courseRepository.save(course);

        Optional<Course> found = courseRepository.findById(course.getId());

        assertTrue(found.isPresent());
        assertEquals(course.getId(), found.get().getId());
        assertEquals("Clean Architecture na Prática", found.get().getTitle());
        assertEquals(CourseStatusType.DRAFT, found.get().getStatus());
    }

    @Test
    @DisplayName("Deve retornar vazio quando curso não encontrado pelo id")
    void shouldReturnEmptyWhenCourseNotFoundById() {
        Optional<Course> found = courseRepository.findById(new CourseId(UUID.randomUUID()));

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar todos os cursos")
    void shouldReturnAllCourses() {
        Course firstCourse = buildDraftCourse("Clean Architecture na Prática");
        Course secondCourse = buildDraftCourse("DDD com Java");
        courseRepository.save(firstCourse);
        courseRepository.save(secondCourse);

        List<Course> courses = courseRepository.findAll();

        assertEquals(2, courses.size());
        assertTrue(courses.stream().anyMatch(c -> c.getTitle().equals("Clean Architecture na Prática")));
        assertTrue(courses.stream().anyMatch(c -> c.getTitle().equals("DDD com Java")));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existem cursos")
    void shouldReturnEmptyListWhenNoCoursesExist() {
        List<Course> courses = courseRepository.findAll();

        assertNotNull(courses);
        assertTrue(courses.isEmpty());
    }

    @Test
    @DisplayName("Deve salvar curso com módulos, aulas e assessment")
    void shouldSaveCourseWithModulesLessonsAndAssessment() {
        Course course = buildPublishedCourse("Clean Architecture na Prática");
        courseRepository.save(course);

        Optional<Course> found = courseRepository.findById(course.getId());

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getModules().size());
        assertEquals("Fundamentos", found.get().getModules().getFirst().getTitle());
        assertEquals(1, found.get().getModules().getFirst().getLessons().size());
        assertEquals("Introdução", found.get().getModules().getFirst().getLessons().getFirst().getTitle());
        assertNotNull(found.get().getAssessment());
        assertEquals("Prova Final", found.get().getAssessment().getTitle());
        assertEquals(new BigDecimal("6.00"), found.get().getAssessment().getMinimumGrade());
    }

    @Test
    @DisplayName("Deve atualizar curso ao salvar com mesmo id")
    void shouldUpdateCourseWhenSavingWithSameId() {
        Course course = buildDraftCourse("Clean Architecture na Prática");
        courseRepository.save(course);

        Course updated = Course.restore(
                course.getId(),
                "Clean Architecture Avançado",
                "Nova descrição",
                instructorId,
                Price.of(new BigDecimal("299.90"), CurrencyType.BRL),
                30,
                CourseStatusType.DRAFT,
                new ArrayList<>(),
                null,
                null
        );
        courseRepository.save(updated);

        Optional<Course> found = courseRepository.findById(course.getId());

        assertTrue(found.isPresent());
        assertEquals("Clean Architecture Avançado", found.get().getTitle());
        assertEquals(new BigDecimal("299.90"), found.get().getPrice().amount());
    }
}