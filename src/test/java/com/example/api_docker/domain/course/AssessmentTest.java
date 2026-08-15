package com.example.api_docker.domain.course;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.shared.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentTest extends UnitAbstractTests {

    @Test
    @DisplayName("Deve criar avaliação com sucesso gerando id automaticamente")
    void shouldCreateAssessmentSuccessfullyWithGeneratedId() {
        Assessment assessment = new Assessment(
                "Prova Final",
                new BigDecimal("7.00"),
                new BigDecimal("10.00")
        );

        assertNotNull(assessment.getId());
        assertEquals("Prova Final", assessment.getTitle());
        assertEquals(new BigDecimal("7.00"), assessment.getMinimumGrade());
        assertEquals(new BigDecimal("10.00"), assessment.getMaximumGrade());
    }

    @Test
    @DisplayName("Deve criar avaliação com sucesso utilizando id informado")
    void shouldCreateAssessmentWithProvidedId() {
        AssessmentId id = AssessmentId.generate();
        Assessment assessment = new Assessment(
                id,
                "Prova Final",
                new BigDecimal("6.00"),
                new BigDecimal("10.00")
        );

        assertEquals(id, assessment.getId());
        assertEquals("Prova Final", assessment.getTitle());
        assertEquals(new BigDecimal("6.00"), assessment.getMinimumGrade());
        assertEquals(new BigDecimal("10.00"), assessment.getMaximumGrade());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o título for nulo ou em branco")
    void shouldThrowExceptionWhenTitleIsInvalid() {
        DomainException nullTitleEx = assertThrows(
                DomainException.class,
                () -> new Assessment(null, new BigDecimal("7.00"), new BigDecimal("10.00"))
        );
        assertEquals("Título da avaliação não pode ser vazio", nullTitleEx.getMessage());

        DomainException blankTitleEx = assertThrows(
                DomainException.class,
                () -> new Assessment("   ", new BigDecimal("7.00"), new BigDecimal("10.00"))
        );
        assertEquals("Título da avaliação não pode ser vazio", blankTitleEx.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nota mínima ou nota máxima for nula")
    void shouldThrowExceptionWhenGradesAreNull() {
        DomainException nullMinEx = assertThrows(
                DomainException.class,
                () -> new Assessment("Prova Final", null, new BigDecimal("10.00"))
        );
        assertEquals("Notas não podem ser nulas", nullMinEx.getMessage());

        DomainException nullMaxEx = assertThrows(
                DomainException.class,
                () -> new Assessment("Prova Final", new BigDecimal("7.00"), null)
        );
        assertEquals("Notas não podem ser nulas", nullMaxEx.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nota mínima for negativa")
    void shouldThrowExceptionWhenMinimumGradeIsNegative() {
        DomainException negativeMinEx = assertThrows(
                DomainException.class,
                () -> new Assessment("Prova Final", new BigDecimal("-1.00"), new BigDecimal("10.00"))
        );
        assertEquals("Nota mínima não pode ser negativa", negativeMinEx.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nota mínima for maior ou igual à nota máxima")
    void shouldThrowExceptionWhenMinimumGradeIsGreaterOrEqualToMaximumGrade() {
        DomainException equalGradesEx = assertThrows(
                DomainException.class,
                () -> new Assessment("Prova Final", new BigDecimal("10.00"), new BigDecimal("10.00"))
        );
        assertEquals("Nota mínima deve ser menor que a nota máxima", equalGradesEx.getMessage());

        DomainException greaterMinEx = assertThrows(
                DomainException.class,
                () -> new Assessment("Prova Final", new BigDecimal("10.00"), new BigDecimal("7.00"))
        );
        assertEquals("Nota mínima deve ser menor que a nota máxima", greaterMinEx.getMessage());
    }

    @Test
    @DisplayName("Deve verificar se a nota é suficiente para aprovação")
    void shouldValidatePassingGradeCorrectly() {
        Assessment assessment = new Assessment(
                "Prova Final",
                new BigDecimal("7.00"),
                new BigDecimal("10.00")
        );

        assertTrue(assessment.isPassingGrade(new BigDecimal("7.00")));
        assertTrue(assessment.isPassingGrade(new BigDecimal("8.50")));
        assertFalse(assessment.isPassingGrade(new BigDecimal("6.99")));
        assertFalse(assessment.isPassingGrade(null));
    }
}
