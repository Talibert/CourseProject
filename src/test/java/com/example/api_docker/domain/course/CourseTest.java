package com.example.api_docker.domain.course;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.course.event.CourseCreatedEvent;
import com.example.api_docker.domain.course.event.CoursePublishedEvent;
import com.example.api_docker.domain.course.exception.CourseAlreadyPublishedException;
import com.example.api_docker.domain.course.exception.CoursePublishNotAllowedException;
import com.example.api_docker.domain.course.exception.DuplicateModuleOrderException;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest extends UnitAbstractTests {

    private InstructorId instructorId;
    private Price defaultPrice;

    @BeforeEach
    void setUp() {
        instructorId = new InstructorId(UUID.randomUUID());
        defaultPrice = Price.of(new BigDecimal("199.90"), CurrencyType.BRL);
    }

    @Test
    @DisplayName("Deve criar curso em estado DRAFT com evento CourseCreatedEvent registrado")
    void shouldCreateCourseInDraftStatusWithCreatedEvent() {
        Course course = Course.create(
                "Clean Architecture",
                "Aprenda Clean Arch na Prática",
                instructorId,
                defaultPrice,
                40
        );

        assertNotNull(course.getId());
        assertEquals("Clean Architecture", course.getTitle());
        assertEquals("Aprenda Clean Arch na Prática", course.getDescription());
        assertEquals(instructorId, course.getInstructorId());
        assertEquals(defaultPrice, course.getPrice());
        assertEquals(40, course.getEstimatedHours());
        assertEquals(CourseStatusType.DRAFT, course.getStatus());
        assertTrue(course.getModules().isEmpty());
        assertNull(course.getAssessment());
        assertNull(course.getPublishedAt());

        List<DomainEvent> events = course.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(CourseCreatedEvent.class, events.getFirst());

        // Garantir que pullDomainEvents limpa a lista de eventos
        assertTrue(course.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar curso com título inválido")
    void shouldThrowExceptionWhenTitleIsInvalid() {
        DomainException exceptionNull = assertThrows(
                DomainException.class,
                () -> Course.create(null, "Descrição", instructorId, defaultPrice, 10)
        );
        assertEquals("Título do curso não pode ser vazio", exceptionNull.getMessage());

        DomainException exceptionBlank = assertThrows(
                DomainException.class,
                () -> Course.create("   ", "Descrição", instructorId, defaultPrice, 10)
        );
        assertEquals("Título do curso não pode ser vazio", exceptionBlank.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar curso com carga horária menor ou igual a zero")
    void shouldThrowExceptionWhenEstimatedHoursIsInvalid() {
        DomainException exceptionZero = assertThrows(
                DomainException.class,
                () -> Course.create("Título", "Descrição", instructorId, defaultPrice, 0)
        );
        assertEquals("Carga horária deve ser maior que zero", exceptionZero.getMessage());

        DomainException exceptionNegative = assertThrows(
                DomainException.class,
                () -> Course.create("Título", "Descrição", instructorId, defaultPrice, -5)
        );
        assertEquals("Carga horária deve ser maior que zero", exceptionNegative.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar módulo ao curso em DRAFT")
    void shouldAddModuleToCourseInDraft() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);

        Module module = Module.create("Módulo 1", 1);
        course.addModule(module);

        assertEquals(1, course.getModules().size());
        assertEquals("Módulo 1", course.getModules().getFirst().getTitle());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar módulo com ordem duplicada")
    void shouldThrowExceptionWhenAddingModuleWithDuplicateOrder() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);

        Module module1 = Module.create("Módulo 1", 1);
        Module module2 = Module.create("Módulo Duplicado", 1);

        course.addModule(module1);

        assertThrows(
                DuplicateModuleOrderException.class,
                () -> course.addModule(module2)
        );
    }

    @Test
    @DisplayName("Deve definir prova final no curso em DRAFT")
    void shouldDefineAssessmentInDraft() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);

        Assessment assessment = new Assessment(
                AssessmentId.generate(),
                "Prova Final",
                new BigDecimal("7.00"),
                new BigDecimal("10.00")
        );

        course.defineAssessment(assessment);

        assertNotNull(course.getAssessment());
        assertEquals("Prova Final", course.getAssessment().getTitle());
    }

    @Test
    @DisplayName("Deve publicar curso com sucesso quando atender a todos os requisitos")
    void shouldPublishCourseSuccessfully() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);

        Module module = Module.create("Módulo 1", 1);
        module.addLesson(Lesson.create("Aula 1", 1, 15));
        course.addModule(module);

        Assessment assessment = new Assessment(
                AssessmentId.generate(),
                "Prova Final",
                new BigDecimal("6.00"),
                new BigDecimal("10.00")
        );
        course.defineAssessment(assessment);

        // Limpa os eventos de criação antes de publicar
        course.pullDomainEvents();

        course.publish();

        assertEquals(CourseStatusType.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt());

        List<DomainEvent> events = course.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(CoursePublishedEvent.class, events.getFirst());
    }

    @Test
    @DisplayName("Deve lançar exceção ao publicar curso sem módulos")
    void shouldThrowExceptionWhenPublishingWithoutModules() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);

        Assessment assessment = new Assessment(
                AssessmentId.generate(),
                "Prova Final",
                new BigDecimal("6.00"),
                new BigDecimal("10.00")
        );
        course.defineAssessment(assessment);

        CoursePublishNotAllowedException exception = assertThrows(
                CoursePublishNotAllowedException.class,
                course::publish
        );
        assertEquals("O curso precisa ter ao menos um módulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao publicar curso sem aulas nos módulos")
    void shouldThrowExceptionWhenPublishingWithoutLessons() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);
        course.addModule(Module.create("Módulo Sem Aulas", 1));

        Assessment assessment = new Assessment(
                AssessmentId.generate(),
                "Prova Final",
                new BigDecimal("6.00"),
                new BigDecimal("10.00")
        );
        course.defineAssessment(assessment);

        CoursePublishNotAllowedException exception = assertThrows(
                CoursePublishNotAllowedException.class,
                course::publish
        );
        assertEquals("O curso precisa ter ao menos uma aula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao publicar curso sem prova final")
    void shouldThrowExceptionWhenPublishingWithoutAssessment() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);

        Module module = Module.create("Módulo 1", 1);
        module.addLesson(Lesson.create("Aula 1", 1, 15));
        course.addModule(module);

        CoursePublishNotAllowedException exception = assertThrows(
                CoursePublishNotAllowedException.class,
                course::publish
        );
        assertEquals("O curso precisa ter uma prova final", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar publicar um curso já publicado")
    void shouldThrowExceptionWhenPublishingAlreadyPublishedCourse() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);
        Module module = Module.create("Módulo 1", 1);
        module.addLesson(Lesson.create("Aula 1", 1, 15));
        course.addModule(module);
        course.defineAssessment(new Assessment(
                AssessmentId.generate(),
                "Prova Final",
                new BigDecimal("6.00"),
                new BigDecimal("10.00")
        ));
        course.publish();

        assertThrows(
                CourseAlreadyPublishedException.class,
                course::publish
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar curso (adicionar módulo) após publicado")
    void shouldThrowExceptionWhenModifyingPublishedCourse() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);
        Module module = Module.create("Módulo 1", 1);
        module.addLesson(Lesson.create("Aula 1", 1, 15));
        course.addModule(module);
        course.defineAssessment(new Assessment(
                AssessmentId.generate(),
                "Prova Final",
                new BigDecimal("6.00"),
                new BigDecimal("10.00")
        ));
        course.publish();

        assertThrows(
                CourseAlreadyPublishedException.class,
                () -> course.addModule(Module.create("Módulo 2", 2))
        );
    }

    @Test
    @DisplayName("Deve calcular corretamente o total de aulas do curso")
    void shouldCalculateTotalLessonsCorrectly() {
        Course course = Course.create("Título", "Descrição", instructorId, defaultPrice, 20);

        Module m1 = Module.create("Módulo 1", 1);
        m1.addLesson(Lesson.create("Aula 1.1", 1, 10));
        m1.addLesson(Lesson.create("Aula 1.2", 2, 15));

        Module m2 = Module.create("Módulo 2", 2);
        m2.addLesson(Lesson.create("Aula 2.1", 1, 20));

        course.addModule(m1);
        course.addModule(m2);

        assertEquals(3, course.getTotalLessons());
    }
}
