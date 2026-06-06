package com.example.api_docker.infra.persistence.enrollment;

import com.example.api_docker.domain.enrollment.EnrollmentStatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// adapters/out/persistence/enrollment/EnrollmentJpaEntity.java
@Entity
@Table(name = "enrollments")
@Getter
@Setter
public class EnrollmentJpaEntity {

    @Id
    @Column(name = "enrollment_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "student_id", nullable = false, columnDefinition = "uuid")
    private UUID studentId;

    @Column(name = "course_id", nullable = false, columnDefinition = "uuid")
    private UUID courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatusType status;

    @ElementCollection
    @CollectionTable(name = "enrollment_completed_lessons", joinColumns = @JoinColumn(name = "enrollment_id"))
    @Column(name = "lesson_id", columnDefinition = "uuid")
    private Set<UUID> completedLessons;

    @ElementCollection
    @CollectionTable(name = "enrollment_grades", joinColumns = @JoinColumn(name = "enrollment_id"))
    @MapKeyColumn(name = "assessment_id", columnDefinition = "uuid")
    @Column(name = "grade", precision = 5, scale = 2)
    private Map<UUID, BigDecimal> grades;

    @Column(name = "total_lessons", nullable = false)
    private int totalLessons;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected EnrollmentJpaEntity() {}
}
