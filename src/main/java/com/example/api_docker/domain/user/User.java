package com.example.api_docker.domain.user;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
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
}
