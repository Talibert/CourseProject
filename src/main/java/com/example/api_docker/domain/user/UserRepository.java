package com.example.api_docker.domain.user;

public interface UserRepository {
    boolean existsByEmail(Email email);
}
