package com.example.api_docker.infra.persistence.user;

import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.UserRepository;
import com.example.api_docker.infra.persistence.admin.AdminJpaRepository;
import com.example.api_docker.infra.persistence.instructor.InstructorJpaRepository;
import com.example.api_docker.infra.persistence.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRepositoryImpl implements UserRepository {

    private final StudentJpaRepository studentJpaRepository;
    private final InstructorJpaRepository instructorJpaRepository;
    private final AdminJpaRepository adminJpaRepository;

    @Override
    public boolean existsByEmail(Email email) {
        return studentJpaRepository.existsByEmail(email.value()) ||
                instructorJpaRepository.existsByEmail(email.value()) ||
                adminJpaRepository.existsByEmail(email.value());
    }
}
