package com.example.api_docker.domain.instructor;

import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.UserId;

import java.util.Optional;

public interface InstructorRepository {
    void save(Instructor instructor);
    Optional<Instructor> findById(UserId id);
    Optional<Instructor> findByEmail(Email email);
    boolean existsByEmail(Email email);
    boolean existsById(UserId id);
}
