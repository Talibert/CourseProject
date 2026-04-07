package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Assessment {

    private final AssessmentId id;
    private final String title;
    private final BigDecimal minimumGrade;
    private final BigDecimal maximumGrade;

    public Assessment(String title, BigDecimal minimumGrade, BigDecimal maximumGrade) {
        validate(title, minimumGrade, maximumGrade);
        this.id = AssessmentId.generate();
        this.title = title;
        this.minimumGrade = minimumGrade;
        this.maximumGrade = maximumGrade;
    }

    public Assessment(AssessmentId id, String title, BigDecimal minimumGrade, BigDecimal maximumGrade) {
        validate(title, minimumGrade, maximumGrade);
        this.id = id;
        this.title = title;
        this.minimumGrade = minimumGrade;
        this.maximumGrade = maximumGrade;
    }

    private static void validate(String title, BigDecimal minimumGrade, BigDecimal maximumGrade) {
        if (title == null || title.isBlank())
            throw new DomainException("Título da avaliação não pode ser vazio");

        if (minimumGrade == null || maximumGrade == null)
            throw new DomainException("Notas não podem ser nulas");

        if (minimumGrade.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainException("Nota mínima não pode ser negativa");

        if (minimumGrade.compareTo(maximumGrade) >= 0)
            throw new DomainException("Nota mínima deve ser menor que a nota máxima");
    }

    public boolean isPassingGrade(BigDecimal grade) {
        return grade != null && grade.compareTo(minimumGrade) >= 0;
    }
}
