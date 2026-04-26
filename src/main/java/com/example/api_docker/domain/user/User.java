package com.example.api_docker.domain.user;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import com.example.api_docker.domain.student.Email;
import com.example.api_docker.domain.student.FullName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class User {

    private final UserId id;
    private final FullName name;
    private final Email email;
    private String passwordHash;
    private final LocalDateTime createdAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected User(UserId id, FullName name, Email email,
                   String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank())
            throw new DomainException("Hash da senha não pode ser vazio");

        this.passwordHash = newPasswordHash;
        domainEvents.add(onPasswordChanged());
    }

    // Cada subclasse define o evento correto
    protected abstract DomainEvent onPasswordChanged();

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    protected void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public UserId getId()            { return id; }
    public FullName getName()        { return name; }
    public Email getEmail()          { return email; }
    public String getPasswordHash()  { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
