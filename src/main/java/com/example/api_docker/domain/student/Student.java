package com.example.api_docker.domain.student;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import com.example.api_docker.domain.student.event.*;
import com.example.api_docker.domain.student.exception.InvalidStudentTransitionException;
import com.example.api_docker.domain.user.User;
import com.example.api_docker.domain.user.UserId;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    @Getter
    private final Cpf cpf;
    @Getter
    private final LocalDate birthDate;
    @Getter
    private StudentStatus status;
    @Getter
    private final LocalDateTime createdAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Student(UserId id, FullName name, Email email, Cpf cpf,
                    LocalDate birthDate, String passwordHash,
                    StudentStatus status, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, createdAt);
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Student create(FullName name, Email email, Cpf cpf,
                                 LocalDate birthDate, String passwordHash) {
        Student student = new Student(UserId.generate(), name, email,
                cpf, birthDate, passwordHash, StudentStatus.ACTIVE, LocalDateTime.now());

        student.domainEvents.add(new StudentRegisteredEvent(student.getId(), email));
        return student;
    }

    public static Student restore(UserId id, FullName name, Email email, Cpf cpf,
                                  LocalDate birthDate, String passwordHash,
                                  StudentStatus status, LocalDateTime createdAt) {
        return new Student(id, name, email, cpf, birthDate,
                passwordHash, status, createdAt);
    }

    @Override
    protected DomainEvent onPasswordChanged() {
        return new StudentPasswordChangedEvent(getId());
    }


    public void suspend() {
        if (status != StudentStatus.ACTIVE)
            throw new InvalidStudentTransitionException(status, StudentStatus.SUSPENDED);

        this.status = StudentStatus.SUSPENDED;
        domainEvents.add(new StudentSuspendedEvent(getId()));
    }

    public void ban() {
        if (status == StudentStatus.BANNED)
            throw new InvalidStudentTransitionException(status, StudentStatus.BANNED);

        this.status = StudentStatus.BANNED;
        domainEvents.add(new StudentBannedEvent(getId()));
    }

    public void reactivate() {
        if (status != StudentStatus.SUSPENDED)
            throw new InvalidStudentTransitionException(status, StudentStatus.ACTIVE);

        this.status = StudentStatus.ACTIVE;
        domainEvents.add(new StudentReactivatedEvent(getId()));
    }

    public boolean isActive() { return status == StudentStatus.ACTIVE; }

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

}
