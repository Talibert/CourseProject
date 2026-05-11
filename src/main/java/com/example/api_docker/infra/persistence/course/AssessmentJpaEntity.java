package com.example.api_docker.infra.persistence.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "assessments")
@Getter
@Setter
public class AssessmentJpaEntity {

    @Id
    @Column(name = "assessment_id", columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    private CourseJpaEntity course;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "minimum_grade", nullable = false, precision = 5, scale = 2)
    private BigDecimal minimumGrade;

    @Column(name = "maximum_grade", nullable = false, precision = 5, scale = 2)
    private BigDecimal maximumGrade;

    protected AssessmentJpaEntity() {}
}
