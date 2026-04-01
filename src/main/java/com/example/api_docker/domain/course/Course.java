package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course {

    private final CourseId id;
    private final String title;
    private final String description;
    private final InstructorId instructorId;
    private final Price price;
    private final int estimatedHours;
    private CourseStatus status;
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
        this.status = CourseStatus.DRAFT;
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
        if (status == CourseStatus.PUBLISHED)
            throw new CourseAlreadyPublishedException(id);

        if (modules.isEmpty())
            throw new CoursePublishNotAllowedException("O curso precisa ter ao menos um módulo");

        boolean hasAnyLesson = modules.stream().anyMatch(m -> !m.lessons().isEmpty());
        if (!hasAnyLesson)
            throw new CoursePublishNotAllowedException("O curso precisa ter ao menos uma aula");

        if (assessment == null)
            throw new CoursePublishNotAllowedException("O curso precisa ter uma prova final");

        this.status = CourseStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        domainEvents.add(new CoursePublishedEvent(id));
    }

    // Só pode adicionar módulos/aulas em DRAFT
    public void addModule(Module module) {
        ensureDraft();
        boolean orderExists = modules.stream()
                .anyMatch(m -> m.order() == module.order());
        if (orderExists) {
            throw new DuplicateModuleOrderException(module.order());
        }
        modules.add(module);
    }

    public void defineAssessment(Assessment assessment) {
        ensureDraft();
        Objects.requireNonNull(assessment, "Assessment não pode ser nulo");
        this.assessment = assessment;
    }

    // Gera o snapshot estrutural usado pelo Enrollment
    public CourseStructure toStructure() {
        var moduleStructures = modules.stream()
                .sorted(Comparator.comparingInt(Module::order))
                .map(m -> new ModuleStructure(
                        m.id(),
                        m.title(),
                        m.lessons().stream()
                                .sorted(Comparator.comparingInt(Lesson::order))
                                .map(Lesson::id)
                                .toList()
                ))
                .toList();
        return CourseStructure.of(this.id, moduleStructures);
    }

    public static Course restore(CourseId id, String title, String description,
                                 InstructorId instructorId, Price price, int estimatedHours,
                                 CourseStatus status, List<Module> modules,
                                 Assessment assessment, LocalDateTime publishedAt) {
        var course = new Course(id, title, description, instructorId, price, estimatedHours);
        course.status = status;
        course.modules.addAll(modules);
        course.assessment = assessment;
        course.publishedAt = publishedAt;
        return course;
    }

    private void ensureDraft() {
        if (status != CourseStatus.DRAFT) {
            throw new CourseAlreadyPublishedException(id);
        }
    }

    private static void validate(String title, int estimatedHours) {
        if (title == null || title.isBlank()) {
            throw new DomainException("Título do curso não pode ser vazio");
        }
        if (estimatedHours <= 0) {
            throw new DomainException("Carga horária deve ser maior que zero");
        }
    }

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public CourseId getId()              { return id; }
    public CourseStatus getStatus()      { return status; }
    public List<Module> getModules()     { return List.copyOf(modules); }
    public int totalLessons()            { return modules.stream().mapToInt(m -> m.lessons().size()).sum(); }
}
