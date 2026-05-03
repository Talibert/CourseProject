package com.example.api_docker.domain.student;

import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.UserId;

import java.util.Optional;

public interface StudentRepository {
    void save(Student student);
    Optional<Student> findById(UserId id);
    Optional<Student> findByEmail(Email email);
    boolean existsByCpf(Cpf cpf);
    boolean existsByEmail(Email email);
}
