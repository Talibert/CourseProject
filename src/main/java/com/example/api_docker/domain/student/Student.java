package com.example.api_docker.domain.student;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import com.example.api_docker.domain.student.event.*;
import com.example.api_docker.domain.student.exception.InvalidStudentTransitionException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student {

    @Getter
    private final StudentId id;
    @Getter
    private final FullName name;
    @Getter
    private final Email email;
    @Getter
    private final Cpf cpf;
    @Getter
    private final LocalDate birthDate;
    @Getter
    private String passwordHash;
    @Getter
    private StudentStatus status;
    @Getter
    private final LocalDateTime createdAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Student(StudentId id, FullName name, Email email, Cpf cpf,
                    LocalDate birthDate, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.passwordHash = passwordHash;
        this.status = StudentStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    private Student(StudentId id, FullName name, Email email, Cpf cpf,
                    LocalDate birthDate, String passwordHash,
                    StudentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Student create(FullName name, Email email, Cpf cpf,
                                 LocalDate birthDate, String passwordHash) {
        Student student = new Student(StudentId.generate(), name, email,
                cpf, birthDate, passwordHash);

        student.domainEvents.add(new StudentRegisteredEvent(student.id, email));
        return student;
    }

    public static Student restore(StudentId id, FullName name, Email email, Cpf cpf,
                                  LocalDate birthDate, String passwordHash,
                                  StudentStatus status, LocalDateTime createdAt) {
        return new Student(id, name, email, cpf, birthDate,
                passwordHash, status, createdAt);
    }

    public void suspend() {
        if (status != StudentStatus.ACTIVE)
            throw new InvalidStudentTransitionException(status, StudentStatus.SUSPENDED);

        this.status = StudentStatus.SUSPENDED;
        domainEvents.add(new StudentSuspendedEvent(id));
    }

    public void ban() {
        if (status == StudentStatus.BANNED)
            throw new InvalidStudentTransitionException(status, StudentStatus.BANNED);

        this.status = StudentStatus.BANNED;
        domainEvents.add(new StudentBannedEvent(id));
    }

    public void reactivate() {
        if (status != StudentStatus.SUSPENDED)
            throw new InvalidStudentTransitionException(status, StudentStatus.ACTIVE);

        this.status = StudentStatus.ACTIVE;
        domainEvents.add(new StudentReactivatedEvent(id));
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new DomainException("Hash da senha não pode ser vazio");
        }
        this.passwordHash = newPasswordHash;
        domainEvents.add(new StudentPasswordChangedEvent(id));
    }

    public boolean isActive() { return status == StudentStatus.ACTIVE; }

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

}
