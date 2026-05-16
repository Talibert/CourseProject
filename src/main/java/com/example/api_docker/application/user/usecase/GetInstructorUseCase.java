package com.example.api_docker.application.user.usecase;

import com.example.api_docker.application.user.query.GetInstructorQuery;
import com.example.api_docker.application.user.result.InstructorResult;
import com.example.api_docker.domain.course.exception.InstructorNotFoundException;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetInstructorUseCase {

    private final InstructorRepository instructorRepository;

    public InstructorResult execute(GetInstructorQuery query) {
        Instructor instructor = instructorRepository.findById(query.instructorId())
                .orElseThrow(() -> new InstructorNotFoundException(query.instructorId()));

        return InstructorResult.from(instructor);
    }
}
