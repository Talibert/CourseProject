package com.example.api_docker.domain.user;

import com.example.api_docker.RepositoryAbstractTests;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.instructor.SocialLinks;
import com.example.api_docker.domain.student.Cpf;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.StudentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando email existe para student")
    void shouldReturnTrueWhenEmailExistsForStudent() {
        Student student = Student.restore(
                new UserId(UUID.randomUUID()),
                new FullName(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME),
                new Email(DEFAULT_EMAIL),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                DEFAULT_PASSWORD_HASH,
                StudentStatus.ACTIVE,
                LocalDateTime.now()
        );
        studentRepository.save(student);

        boolean exists = userRepository.existsByEmail(new Email(DEFAULT_EMAIL));

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando email existe para admin")
    void shouldReturnTrueWhenEmailExistsForAdmin() {
        Admin admin = Admin.restore(
                new UserId(UUID.randomUUID()),
                new FullName(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME),
                new Email("admin@email.com"),
                DEFAULT_PASSWORD_HASH,
                LocalDateTime.now()
        );
        adminRepository.save(admin);

        boolean exists = userRepository.existsByEmail(new Email("admin@email.com"));

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando email existe para instructor")
    void shouldReturnTrueWhenEmailExistsForInstructor() {
        Instructor instructor = Instructor.restore(
                new UserId(UUID.randomUUID()),
                new FullName(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME),
                new Email("instructor@email.com"),
                DEFAULT_PASSWORD_HASH,
                "Bio",
                "Specialty",
                null,
                SocialLinks.empty(),
                LocalDateTime.now()
        );
        instructorRepository.save(instructor);

        boolean exists = userRepository.existsByEmail(new Email("instructor@email.com"));

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar falso quando email não existe em nenhuma tabela")
    void shouldReturnFalseWhenEmailNotExistsInAnyTable() {
        boolean exists = userRepository.existsByEmail(new Email("naoexiste@email.com"));

        assertFalse(exists);
    }
}