package com.example.api_docker.domain.enrollment;

import com.example.api_docker.domain.certificate.CertificatePolicy;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.CourseStructure;
import com.example.api_docker.domain.course.LessonId;
import com.example.api_docker.domain.enrollment.event.*;
import com.example.api_docker.domain.enrollment.exception.EnrollmentCompletionNotAllowedException;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotActiveException;
import com.example.api_docker.domain.enrollment.exception.InvalidEnrollmentTransitionException;
import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Enrollment {

    private final EnrollmentId id;
    private final UserId userId;
    private final CourseId courseId;
    private EnrollmentStatusType status;
    private Progress progress;
    private final LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Enrollment(EnrollmentId id, UserId userId, CourseId courseId, int totalCourseLessons) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.status = EnrollmentStatusType.PENDING;
        this.progress = Progress.zero(totalCourseLessons);
        this.enrolledAt = LocalDateTime.now();
    }

    private Enrollment(EnrollmentId id, UserId userId, CourseId courseId,
                       EnrollmentStatusType status, Progress progress,
                       LocalDateTime enrolledAt, LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.status = status;
        this.progress = progress;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
    }

    public static Enrollment create(UserId studentId, CourseId courseId,
                                    CourseStructure structure, BigDecimal amount,
                                    PaymentMethodType paymentMethod, int installments) {
        Enrollment enrollment = new Enrollment(
                EnrollmentId.generate(), studentId, courseId, structure.totalLessons()
        );
        enrollment.addDomainEvent(new EnrollmentCreatedEvent(
                enrollment.id, studentId, courseId, amount, paymentMethod, installments
        ));
        return enrollment;
    }

    public static Enrollment restore(EnrollmentId id, UserId userId, CourseId courseId,
                                     EnrollmentStatusType status, Progress progress,
                                     LocalDateTime enrolledAt, LocalDateTime completedAt) {
        return new Enrollment(id, userId, courseId, status, progress, enrolledAt, completedAt);
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

    public void complete(CertificatePolicy policy) {
        if (status != EnrollmentStatusType.ACTIVE)
            throw new InvalidEnrollmentTransitionException(status, EnrollmentStatusType.COMPLETED);

        if (!policy.isSatisfiedBy(this)) {
            throw new EnrollmentCompletionNotAllowedException(
                    "Progresso insuficiente: %.0f%% (mínimo 70%%) ou nota abaixo do exigido"
                            .formatted(progress.percentage())
            );
        }

        this.status = EnrollmentStatusType.COMPLETED;
        this.completedAt = LocalDateTime.now();
        domainEvents.add(new EnrollmentCompletedEvent(id, userId, courseId));
    }

    public void recordLessonProgress(LessonId lessonId, CourseStructure structure) {
        if (status != EnrollmentStatusType.ACTIVE)
            throw new EnrollmentNotActiveException(id);

        this.progress = progress.withCompletedLesson(lessonId, structure);

        double percentage = progress.percentage();

        if (percentage % 25 == 0 && percentage > 0)
            domainEvents.add(new ProgressMilestoneReachedEvent(id, percentage));
    }

    private void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public boolean isActive()    { return status == EnrollmentStatusType.ACTIVE; }
    public boolean isCompleted() { return status == EnrollmentStatusType.COMPLETED; }
}
