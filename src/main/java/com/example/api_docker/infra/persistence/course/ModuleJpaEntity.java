package com.example.api_docker.infra.persistence.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "modules")
@Getter
@Setter
public class ModuleJpaEntity {

    @Id
    @Column(name = "module_id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseJpaEntity course;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "order", nullable = false)
    private int order;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<LessonJpaEntity> lessons = new ArrayList<>();

    protected ModuleJpaEntity() {}
}
