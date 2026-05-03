package com.example.api_docker.domain.admin;

import com.example.api_docker.domain.admin.event.AdminCreatedEvent;
import com.example.api_docker.domain.admin.event.AdminPasswordChangedEvent;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.User;
import com.example.api_docker.domain.user.UserId;

import java.time.LocalDateTime;

public class Admin extends User {

    private Admin(UserId id, FullName name, Email email,
                  String passwordHash, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, createdAt);
    }

    public static Admin create(FullName name, Email email, String passwordHash) {
        Admin admin = new Admin(
                UserId.generate(), name, email,
                passwordHash, LocalDateTime.now()
        );
        admin.addDomainEvent(new AdminCreatedEvent(admin.getId(), email));
        return admin;
    }

    public static Admin restore(UserId id, FullName name, Email email,
                                String passwordHash, LocalDateTime createdAt) {
        return new Admin(id, name, email, passwordHash, createdAt);
    }

    @Override
    protected DomainEvent onPasswordChanged() {
        return new AdminPasswordChangedEvent(getId());
    }
}
