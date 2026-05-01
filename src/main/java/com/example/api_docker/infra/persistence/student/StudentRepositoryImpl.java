package com.example.api_docker.infra.persistence.student;

import com.example.api_docker.domain.student.*;
import com.example.api_docker.domain.user.UserId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository jpaRepository;

    public StudentRepositoryImpl(StudentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Student student) {
        jpaRepository.save(toJpaEntity(student));
    }

    @Override
    public Optional<Student> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Student> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByCpf(Cpf cpf) {
        return jpaRepository.existsByCpf(cpf.value());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    private StudentJpaEntity toJpaEntity(Student student) {
        var entity = new StudentJpaEntity();
        entity.setId(student.getId().value());
        entity.setFirstName(student.getName().firstName());
        entity.setLastName(student.getName().lastName());
        entity.setEmail(student.getEmail().value());
        entity.setCpf(student.getCpf().value());
        entity.setBirthDate(student.getBirthDate());
        entity.setPasswordHash(student.getPasswordHash());
        entity.setStatus(student.getStatus());
        entity.setCreatedAt(student.getCreatedAt());
        return entity;
    }

    private Student toDomain(StudentJpaEntity entity) {
        return Student.restore(
                new UserId(entity.getId()),
                new FullName(entity.getFirstName(), entity.getLastName()),
                new Email(entity.getEmail()),
                new Cpf(entity.getCpf()),
                entity.getBirthDate(),
                entity.getPasswordHash(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
