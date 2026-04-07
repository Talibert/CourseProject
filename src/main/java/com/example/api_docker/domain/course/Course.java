package com.example.api_docker.domain.course;

import com.example.api_docker.domain.course.event.CourseCreatedEvent;
import com.example.api_docker.domain.course.event.CoursePublishedEvent;
import com.example.api_docker.domain.course.exception.CourseAlreadyPublishedException;
import com.example.api_docker.domain.course.exception.CoursePublishNotAllowedException;
import com.example.api_docker.domain.course.exception.DuplicateModuleOrderException;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Course {

    @Getter
    private final CourseId id;
    @Getter
    private CourseStatusType status;
    private final String title;
    private final String description;
    private final InstructorId instructorId;
    private final Price price;
    private final int estimatedHours;
    private final List<Module> modules;
    private Assessment assessment;
    private LocalDateTime publishedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Course(CourseId id, String title, String description,
                   InstructorId instructorId, Price price, int estimatedHours) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.price = price;
        this.estimatedHours = estimatedHours;
        this.status = CourseStatusType.DRAFT;
        this.modules = new ArrayList<>();
    }

    public static Course create(String title, String description,
                                InstructorId instructorId, Price price, int estimatedHours) {
        validate(title, estimatedHours);
        var course = new Course(CourseId.generate(), title, description,
                instructorId, price, estimatedHours);
        course.domainEvents.add(new CourseCreatedEvent(course.id, instructorId));
        return course;
    }

    // Regra central — só pode publicar se tiver ao menos um módulo com uma aula
    public void publish() {
        if (status == CourseStatusType.PUBLISHED)
            throw new CourseAlreadyPublishedException(id);

        if (modules.isEmpty())
            throw new CoursePublishNotAllowedException("O curso precisa ter ao menos um módulo");

        boolean hasAnyLesson = modules.stream().anyMatch(m -> !m.getLessons().isEmpty());
        if (!hasAnyLesson)
            throw new CoursePublishNotAllowedException("O curso precisa ter ao menos uma aula");

        if (assessment == null)
            throw new CoursePublishNotAllowedException("O curso precisa ter uma prova final");

        this.status = CourseStatusType.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        domainEvents.add(new CoursePublishedEvent(id));
    }

    // Só pode adicionar módulos/aulas em DRAFT
    public void addModule(Module module) {
        ensureDraft();

        boolean orderExists = modules.stream().anyMatch(m -> m.getOrder() == module.getOrder());

        if(orderExists)
            throw new DuplicateModuleOrderException(module.getOrder());

        modules.add(module);
    }

    public void defineAssessment(Assessment assessment) {
        ensureDraft();

        Objects.requireNonNull(assessment, "Assessment não pode ser nulo");

        this.assessment = assessment;
    }

    public CourseStructure toStructure() {
        var moduleStructures = modules.stream()
                .sorted(Comparator.comparingInt(Module::getOrder))
                .map(Module::toStructure)
                .toList();

        return CourseStructure.of(this.id, moduleStructures);
    }

    public static Course restore(CourseId id, String title, String description,
                                 InstructorId instructorId, Price price, int estimatedHours,
                                 CourseStatusType status, List<Module> modules,
                                 Assessment assessment, LocalDateTime publishedAt) {
        var course = new Course(id, title, description, instructorId, price, estimatedHours);
        course.status = status;
        course.modules.addAll(modules);
        course.assessment = assessment;
        course.publishedAt = publishedAt;
        return course;
    }

    private void ensureDraft() {
        if (status != CourseStatusType.DRAFT)
            throw new CourseAlreadyPublishedException(id);
    }

    private static void validate(String title, int estimatedHours) {
        if (title == null || title.isBlank())
            throw new DomainException("Título do curso não pode ser vazio");

        if (estimatedHours <= 0)
            throw new DomainException("Carga horária deve ser maior que zero");
    }

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public List<Module> getModules(){
        return List.copyOf(modules);
    }

    public int getTotalLessons(){
        return modules.stream().mapToInt(m -> m.getLessons().size()).sum();
    }
}
