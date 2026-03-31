package com.example.api_docker.domain.enrollment;

import com.example.api_docker.domain.certificate.CertificatePolicy;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.CourseStructure;
import com.example.api_docker.domain.course.LessonId;
import com.example.api_docker.domain.enrollment.event.*;
import com.example.api_docker.domain.enrollment.exception.EnrollmentCompletionNotAllowedException;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotActiveException;
import com.example.api_docker.domain.enrollment.exception.InvalidEnrollmentTransitionException;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.StudentId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// domain/enrollment/Enrollment.java
public class Enrollment {

    private final EnrollmentId id;
    private final StudentId studentId;
    private final CourseId courseId;
    private EnrollmentStatusType status;
    private Progress progress;
    private final LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // Construtor privado — só factories criam Enrollment
    private Enrollment(EnrollmentId id, StudentId studentId, CourseId courseId, CourseStructure courseStructure) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = EnrollmentStatusType.PENDING;
        this.progress = Progress.zero(courseStructure);
        this.enrolledAt = LocalDateTime.now();
    }

    public static Enrollment create(StudentId studentId, CourseId courseId, CourseStructure courseStructure) {
        Enrollment enrollment = new Enrollment(EnrollmentId.generate(), studentId, courseId, courseStructure);
        enrollment.domainEvents.add(new EnrollmentCreatedEvent(enrollment.id, studentId, courseId));
        return enrollment;
    }

    public void activate() {
        if (status != EnrollmentStatusType.PENDING)
            throw new InvalidEnrollmentTransitionException(status, EnrollmentStatusType.ACTIVE);

        this.status = EnrollmentStatusType.ACTIVE;
        domainEvents.add(new EnrollmentActivatedEvent(id));
    }

    public void suspend(SuspensionReason reason) {
        if (status != EnrollmentStatusType.ACTIVE)
            throw new InvalidEnrollmentTransitionException(status, EnrollmentStatusType.SUSPENDED);

        this.status = EnrollmentStatusType.SUSPENDED;
        domainEvents.add(new EnrollmentSuspendedEvent(id, reason));
    }

    public void reactivate() {
        if (status != EnrollmentStatusType.SUSPENDED)
            throw new InvalidEnrollmentTransitionException(status, EnrollmentStatusType.ACTIVE);

        this.status = EnrollmentStatusType.ACTIVE;
        domainEvents.add(new EnrollmentReactivatedEvent(id));
    }

    public void cancel(CancellationReason reason) {
        if (status == EnrollmentStatusType.COMPLETED || status == EnrollmentStatusType.CANCELLED)
            throw new InvalidEnrollmentTransitionException(status, EnrollmentStatusType.CANCELLED);

        this.status = EnrollmentStatusType.CANCELLED;
        domainEvents.add(new EnrollmentCancelledEvent(id, reason));
    }

    public void complete(CertificatePolicy policy, CourseStructure courseStructure) {
        if (status != EnrollmentStatusType.ACTIVE)
            throw new InvalidEnrollmentTransitionException(status, EnrollmentStatusType.COMPLETED);

        // A regra de negócio vive aqui — não no use case, não no service
        if (!policy.isSatisfiedBy(this, courseStructure)) {
            throw new EnrollmentCompletionNotAllowedException(
                    "Progresso insuficiente: %.0f%% (mínimo 70%%) ou nota abaixo do exigido"
                            .formatted(progress.percentage(courseStructure))
            );
        }

        this.status = EnrollmentStatusType.COMPLETED;
        this.completedAt = LocalDateTime.now();
        domainEvents.add(new EnrollmentCompletedEvent(id, studentId, courseId));
    }

    public void recordLessonProgress(LessonId lessonId, CourseStructure structure) {
        if (status != EnrollmentStatusType.ACTIVE)
            throw new EnrollmentNotActiveException(id);

        this.progress = progress.withCompletedLesson(lessonId, structure);

        double percentage = progress.percentage(structure);

        if (percentage % 25 == 0 && percentage > 0)
            domainEvents.add(new ProgressMilestoneReachedEvent(id, percentage));
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public boolean isActive()    { return status == EnrollmentStatusType.ACTIVE; }
    public boolean isCompleted() { return status == EnrollmentStatusType.COMPLETED; }
    public Progress getProgress(){ return progress; }
}
