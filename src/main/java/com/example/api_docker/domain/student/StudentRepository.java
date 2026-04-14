package com.example.api_docker.domain.student;

import java.util.Optional;

public interface StudentRepository {
    void save(Student student);
    Optional<Student> findById(StudentId id);
    Optional<Student> findByEmail(Email email);
    boolean existsByCpf(Cpf cpf);
}
