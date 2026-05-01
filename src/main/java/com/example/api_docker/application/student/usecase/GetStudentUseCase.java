package com.example.api_docker.application.student.usecase;

import com.example.api_docker.application.student.query.GetStudentQuery;
import com.example.api_docker.application.student.result.StudentResult;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetStudentUseCase {

    private final StudentRepository studentRepository;

    public StudentResult execute(GetStudentQuery query) {
        var student = studentRepository.findById(query.userId())
                .orElseThrow(() -> new StudentNotFoundException(query.userId()));

        return new StudentResult(
                student.getId().value(),
                student.getName().full(),
                student.getEmail().value(),
                student.getStatus(),
                student.getCreatedAt()
        );
    }
}
