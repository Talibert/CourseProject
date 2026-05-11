package com.example.api_docker.infra.persistence.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

// infra/persistence/course/LessonJpaEntity.java
@Entity
@Table(name = "lessons")
@Getter
@Setter
public class LessonJpaEntity {

    @Id
    @Column(name = "lesson_id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleJpaEntity module;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "order", nullable = false)
    private int order;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    protected LessonJpaEntity() {}
}
