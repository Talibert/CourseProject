package com.example.api_docker.infra.persistence.course;

import com.example.api_docker.domain.course.CourseStatusType;
import com.example.api_docker.domain.course.CurrencyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class CourseJpaEntity {

    @Id
    @Column(name = "course_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "instructor_id", nullable = false, columnDefinition = "uuid")
    private UUID instructorId;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    @Column(name = "estimated_hours", nullable = false)
    private int estimatedHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatusType status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<ModuleJpaEntity> modules = new ArrayList<>();

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private AssessmentJpaEntity assessment;

    protected CourseJpaEntity() {}
}
