package com.example.api_docker.domain.course;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LessonTest extends UnitAbstractTests {

    @Test
    @DisplayName("Deve criar aula com sucesso usando o método de fábrica create")
    void shouldCreateLessonSuccessfullyUsingFactory() {
        Lesson lesson = Lesson.create("Introdução ao Docker", 1, 15);

        assertNotNull(lesson.getId());
        assertEquals("Introdução ao Docker", lesson.getTitle());
        assertEquals(1, lesson.getOrder());
        assertEquals(15, lesson.getDurationMinutes());
    }

    @Test
    @DisplayName("Deve restaurar aula com sucesso utilizando o método restore")
    void shouldRestoreLessonSuccessfully() {
        LessonId id = LessonId.generate();
        Lesson lesson = Lesson.restore(id, "Docker Compose", 3, 30);

        assertEquals(id, lesson.getId());
        assertEquals("Docker Compose", lesson.getTitle());
        assertEquals(3, lesson.getOrder());
        assertEquals(30, lesson.getDurationMinutes());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a duração da aula for menor ou igual a zero")
    void shouldThrowExceptionWhenDurationIsInvalid() {
        DomainException zeroException = assertThrows(
                DomainException.class,
                () -> Lesson.create("Aula Inválida", 1, 0)
        );
        assertEquals("Duração da aula deve ser maior que zero", zeroException.getMessage());

        DomainException negativeException = assertThrows(
                DomainException.class,
                () -> Lesson.create("Aula Inválida", 1, -10)
        );
        assertEquals("Duração da aula deve ser maior que zero", negativeException.getMessage());
    }
}
