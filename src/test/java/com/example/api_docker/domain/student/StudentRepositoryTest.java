package com.example.api_docker.domain.student;

import com.example.api_docker.RepositoryAbstractTests;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StudentRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private StudentRepository studentRepository;

    private Student buildStudent(String email, String cpf) {
        return Student.restore(
                new UserId(UUID.randomUUID()),
                new FullName(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME),
                new Email(email),
                new Cpf(cpf),
                LocalDate.of(2000, 1, 1),
                DEFAULT_PASSWORD_HASH,
                StudentStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve salvar e encontrar student pelo id")
    void shouldSaveAndFindStudentById() {
        Student student = buildStudent(DEFAULT_EMAIL, "529.982.247-25");
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findById(student.getId());

        assertTrue(found.isPresent());
        assertEquals(student.getId(), found.get().getId());
        assertEquals(DEFAULT_EMAIL, found.get().getEmail().value());
        assertEquals(DEFAULT_FIRST_NAME, found.get().getName().firstName());
        assertEquals(DEFAULT_LAST_NAME, found.get().getName().lastName());
        assertEquals(StudentStatus.ACTIVE, found.get().getStatus());
    }

    @Test
    @DisplayName("Deve salvar e encontrar student pelo email")
    void shouldSaveAndFindStudentByEmail() {
        Student student = buildStudent(DEFAULT_EMAIL, "529.982.247-25");
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findByEmail(new Email(DEFAULT_EMAIL));

        assertTrue(found.isPresent());
        assertEquals(DEFAULT_EMAIL, found.get().getEmail().value());
    }

    @Test
    @DisplayName("Deve retornar vazio quando student não encontrado pelo id")
    void shouldReturnEmptyWhenStudentNotFoundById() {
        Optional<Student> found = studentRepository.findById(new UserId(UUID.randomUUID()));

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar vazio quando student não encontrado pelo email")
    void shouldReturnEmptyWhenStudentNotFoundByEmail() {
        Optional<Student> found = studentRepository.findByEmail(
                new Email("naoexiste@email.com")
        );

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando CPF já existe")
    void shouldReturnTrueWhenCpfAlreadyExists() {
        Student student = buildStudent(DEFAULT_EMAIL, "529.982.247-25");
        studentRepository.save(student);

        boolean exists = studentRepository.existsByCpf(new Cpf("529.982.247-25"));

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar falso quando CPF não existe")
    void shouldReturnFalseWhenCpfNotExists() {
        boolean exists = studentRepository.existsByCpf(new Cpf("529.982.247-25"));

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve atualizar student ao salvar com mesmo id")
    void shouldUpdateStudentWhenSavingWithSameId() {
        Student student = buildStudent(DEFAULT_EMAIL, "529.982.247-25");
        studentRepository.save(student);

        Student updated = Student.restore(
                student.getId(),
                new FullName("Novo", "Nome"),
                new Email("novo@email.com"),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                DEFAULT_PASSWORD_HASH,
                StudentStatus.SUSPENDED,
                student.getCreatedAt()
        );
        studentRepository.save(updated);

        Optional<Student> found = studentRepository.findById(student.getId());

        assertTrue(found.isPresent());
        assertEquals("Novo", found.get().getName().firstName());
        assertEquals("novo@email.com", found.get().getEmail().value());
        assertEquals(StudentStatus.SUSPENDED, found.get().getStatus());
    }

    @Test
    @DisplayName("Deve preservar data de nascimento ao salvar")
    void shouldPreserveBirthDateWhenSaving() {
        Student student = buildStudent(DEFAULT_EMAIL, "529.982.247-25");
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findById(student.getId());

        assertTrue(found.isPresent());
        assertEquals(LocalDate.of(2000, 1, 1), found.get().getBirthDate());
    }
}