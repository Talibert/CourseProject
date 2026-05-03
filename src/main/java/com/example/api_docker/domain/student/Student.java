package com.example.api_docker.domain.student;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.event.*;
import com.example.api_docker.domain.student.exception.InvalidStudentTransitionException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.User;
import com.example.api_docker.domain.user.UserId;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Student extends User {

    @Getter
    private final Cpf cpf;
    @Getter
    private final LocalDate birthDate;
    @Getter
    private StudentStatus status;

    private Student(UserId id, FullName name, Email email, Cpf cpf,
                    LocalDate birthDate, String passwordHash,
                    StudentStatus status, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, createdAt);
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.status = status;
    }

    public static Student create(FullName name, Email email, Cpf cpf,
                                 LocalDate birthDate, String passwordHash) {
        Student student = new Student(UserId.generate(), name, email,
                cpf, birthDate, passwordHash, StudentStatus.ACTIVE, LocalDateTime.now());

        student.addDomainEvent(new StudentRegisteredEvent(student.getId(), email));
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
        addDomainEvent(new StudentSuspendedEvent(getId()));
    }

    public void ban() {
        if (status == StudentStatus.BANNED)
            throw new InvalidStudentTransitionException(status, StudentStatus.BANNED);

        this.status = StudentStatus.BANNED;
        addDomainEvent(new StudentBannedEvent(getId()));
    }

    public void reactivate() {
        if (status != StudentStatus.SUSPENDED)
            throw new InvalidStudentTransitionException(status, StudentStatus.ACTIVE);

        this.status = StudentStatus.ACTIVE;
        addDomainEvent(new StudentReactivatedEvent(getId()));
    }

    public boolean isActive() { return StudentStatus.ACTIVE.equals(status); }

}
