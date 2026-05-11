package com.example.api_docker.infra.persistence.course;

import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.Module;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CourseRepositoryImpl implements CourseRepository {

    private final CourseJpaRepository jpaRepository;

    public CourseRepositoryImpl(CourseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Course course) {
        jpaRepository.save(toJpaEntity(course));
    }

    @Override
    public Optional<Course> findById(CourseId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Course> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private CourseJpaEntity toJpaEntity(Course course) {
        CourseJpaEntity entity = new CourseJpaEntity();
        entity.setId(course.getId().value());
        entity.setTitle(course.getTitle());
        entity.setDescription(course.getDescription());
        entity.setInstructorId(course.getInstructorId().value());
        entity.setPrice(course.getPrice().amount());
        entity.setCurrency(course.getPrice().currency());
        entity.setEstimatedHours(course.getEstimatedHours());
        entity.setStatus(course.getStatus());
        entity.setPublishedAt(course.getPublishedAt());

        List<ModuleJpaEntity> moduleEntities = course.getModules().stream()
                .map(m -> toModuleJpaEntity(m, entity))
                .toList();
        entity.getModules().clear();
        entity.getModules().addAll(moduleEntities);

        if (course.getAssessment() != null) {
            entity.setAssessment(toAssessmentJpaEntity(course.getAssessment(), entity));
        }

        return entity;
    }

    private ModuleJpaEntity toModuleJpaEntity(Module module, CourseJpaEntity courseEntity) {
        var entity = new ModuleJpaEntity();
        entity.setId(module.getId().value());
        entity.setCourse(courseEntity);
        entity.setTitle(module.getTitle());
        entity.setOrder(module.getOrder());

        var lessonEntities = module.getLessons().stream()
                .map(l -> toLessonJpaEntity(l, entity))
                .toList();
        entity.getLessons().clear();
        entity.getLessons().addAll(lessonEntities);

        return entity;
    }

    private LessonJpaEntity toLessonJpaEntity(Lesson lesson, ModuleJpaEntity moduleEntity) {
        var entity = new LessonJpaEntity();
        entity.setId(lesson.getId().value());
        entity.setModule(moduleEntity);
        entity.setTitle(lesson.getTitle());
        entity.setOrder(lesson.getOrder());
        entity.setDurationMinutes(lesson.getDurationMinutes());
        return entity;
    }

    private AssessmentJpaEntity toAssessmentJpaEntity(Assessment assessment,
                                                      CourseJpaEntity courseEntity) {
        var entity = new AssessmentJpaEntity();
        entity.setId(assessment.getId().value());
        entity.setCourse(courseEntity);
        entity.setTitle(assessment.getTitle());
        entity.setMinimumGrade(assessment.getMinimumGrade());
        entity.setMaximumGrade(assessment.getMaximumGrade());
        return entity;
    }

    // ─── JPA → Domínio ──────────────────────────────────────────

    private Course toDomain(CourseJpaEntity entity) {
        var modules = entity.getModules().stream()
                .map(this::toModuleDomain)
                .toList();

        Assessment assessment = null;
        if (entity.getAssessment() != null) {
            assessment = toAssessmentDomain(entity.getAssessment());
        }

        return Course.restore(
                new CourseId(entity.getId()),
                entity.getTitle(),
                entity.getDescription(),
                new InstructorId(entity.getInstructorId()),
                Price.of(entity.getPrice(), entity.getCurrency()),
                entity.getEstimatedHours(),
                entity.getStatus(),
                modules,
                assessment,
                entity.getPublishedAt()
        );
    }

    private Module toModuleDomain(ModuleJpaEntity entity) {
        var lessons = entity.getLessons().stream()
                .map(this::toLessonDomain)
                .toList();

        return Module.restore(
                new ModuleId(entity.getId()),
                entity.getTitle(),
                entity.getOrder(),
                lessons
        );
    }

    private Lesson toLessonDomain(LessonJpaEntity entity) {
        return Lesson.restore(
                new LessonId(entity.getId()),
                entity.getTitle(),
                entity.getOrder(),
                entity.getDurationMinutes()
        );
    }

    private Assessment toAssessmentDomain(AssessmentJpaEntity entity) {
        return new Assessment(
                new AssessmentId(entity.getId()),
                entity.getTitle(),
                entity.getMinimumGrade(),
                entity.getMaximumGrade()
        );
    }
}