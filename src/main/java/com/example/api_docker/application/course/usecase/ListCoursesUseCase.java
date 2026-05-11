package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.domain.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ListCoursesUseCase {

    private final CourseRepository courseRepository;

    public List<CourseResult> execute() {
        return courseRepository.findAll().stream()
                .map(CourseResult::from)
                .toList();
    }
}
