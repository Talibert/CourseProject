package com.example.api_docker.domain.course;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.course.exception.DuplicateLessonOrderException;
import com.example.api_docker.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModuleTest extends UnitAbstractTests {

    @Test
    @DisplayName("Deve criar módulo com sucesso via construtor público")
    void shouldCreateModuleSuccessfullyUsingConstructor() {
        Module module = new Module("Módulo 1: Introdução ao Docker", 1);

        assertNotNull(module.getId());
        assertEquals("Módulo 1: Introdução ao Docker", module.getTitle());
        assertEquals(1, module.getOrder());
        assertTrue(module.getLessons().isEmpty());
    }

    @Test
    @DisplayName("Deve criar módulo com sucesso usando o método de fábrica create")
    void shouldCreateModuleSuccessfullyUsingFactoryMethod() {
        Module module = Module.create("Módulo 1: Introdução ao Docker", 1);

        assertNotNull(module.getId());
        assertEquals("Módulo 1: Introdução ao Docker", module.getTitle());
        assertEquals(1, module.getOrder());
        assertTrue(module.getLessons().isEmpty());
    }

    @Test
    @DisplayName("Deve restaurar módulo com sucesso utilizando o método restore")
    void shouldRestoreModuleSuccessfully() {
        ModuleId id = ModuleId.generate();
        Lesson lesson1 = Lesson.create("Aula 1: Conceitos Básicos", 1, 15);
        Lesson lesson2 = Lesson.create("Aula 2: Instalação", 2, 20);

        Module module = Module.restore(id, "Módulo Restaurado", 2, List.of(lesson1, lesson2));

        assertEquals(id, module.getId());
        assertEquals("Módulo Restaurado", module.getTitle());
        assertEquals(2, module.getOrder());
        assertEquals(2, module.getLessons().size());
        assertEquals("Aula 1: Conceitos Básicos", module.getLessons().get(0).getTitle());
        assertEquals("Aula 2: Instalação", module.getLessons().get(1).getTitle());
    }

    @Test
    @DisplayName("Deve adicionar aula ao módulo com sucesso")
    void shouldAddLessonSuccessfully() {
        Module module = Module.create("Módulo 1: Introdução", 1);
        Lesson lesson1 = Lesson.create("Aula 1: Visão Geral", 1, 10);
        Lesson lesson2 = Lesson.create("Aula 2: Primeiros Passos", 2, 15);

        module.addLesson(lesson1);
        module.addLesson(lesson2);

        List<Lesson> lessons = module.getLessons();
        assertEquals(2, lessons.size());
        assertTrue(lessons.contains(lesson1));
        assertTrue(lessons.contains(lesson2));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar aula com ordem duplicada no módulo")
    void shouldThrowExceptionWhenAddingLessonWithDuplicateOrder() {
        Module module = Module.create("Módulo 1: Introdução", 1);
        Lesson lesson1 = Lesson.create("Aula 1: Visão Geral", 1, 10);
        Lesson duplicateOrderLesson = Lesson.create("Aula 1 Duplicada", 1, 20);

        module.addLesson(lesson1);

        assertThrows(
                DuplicateLessonOrderException.class,
                () -> module.addLesson(duplicateOrderLesson)
        );
    }

    @Test
    @DisplayName("Deve retornar uma lista imutável de aulas ao chamar getLessons")
    void shouldReturnUnmodifiableListOfLessons() {
        Module module = Module.create("Módulo 1: Introdução", 1);
        Lesson lesson = Lesson.create("Aula 1", 1, 10);
        module.addLesson(lesson);

        List<Lesson> lessons = module.getLessons();
        Lesson newLesson = Lesson.create("Aula 2", 2, 15);

        assertThrows(
                UnsupportedOperationException.class,
                () -> lessons.add(newLesson)
        );
    }

    @Test
    @DisplayName("Deve converter o módulo para estrutura ordenando os IDs das aulas por ordem")
    void shouldConvertToStructureSuccessfullySortingLessonsByOrder() {
        Module module = Module.create("Módulo 1: Docker Fundamentals", 1);

        Lesson lesson3 = Lesson.create("Aula 3: Volumes", 3, 25);
        Lesson lesson1 = Lesson.create("Aula 1: Introdução", 1, 10);
        Lesson lesson2 = Lesson.create("Aula 2: Containers", 2, 15);

        // Adiciona fora de ordem
        module.addLesson(lesson3);
        module.addLesson(lesson1);
        module.addLesson(lesson2);

        ModuleStructure structure = module.toStructure();

        assertNotNull(structure);
        assertEquals(module.getId(), structure.moduleId());
        assertEquals(module.getTitle(), structure.title());
        assertEquals(3, structure.lessons().size());

        // Verifica se a lista de IDs das lessons está ordenada de acordo com a ordem da aula (1, 2, 3)
        assertEquals(lesson1.getId(), structure.lessons().get(0));
        assertEquals(lesson2.getId(), structure.lessons().get(1));
        assertEquals(lesson3.getId(), structure.lessons().get(2));
    }

    @Test
    @DisplayName("Deve lançar exceção ao converter para estrutura se o módulo não tiver aulas")
    void shouldThrowExceptionWhenConvertingToStructureWithNoLessons() {
        Module module = Module.create("Módulo Vazio", 1);

        DomainException exception = assertThrows(
                DomainException.class,
                module::toStructure
        );
        assertEquals("Um módulo precisa de ao menos uma lesson.", exception.getMessage());
    }
}
