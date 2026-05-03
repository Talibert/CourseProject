package com.example.api_docker.domain.admin;

import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.UserId;

import java.util.Optional;

public interface AdminRepository {
    void save(Admin admin);
    Optional<Admin> findById(UserId id);
    Optional<Admin> findByEmail(Email email);
    boolean existsByEmail(Email email);
}
