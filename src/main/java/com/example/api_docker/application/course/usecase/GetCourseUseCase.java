package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.query.GetCourseQuery;
import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetCourseUseCase {

    private final CourseRepository courseRepository;

    public CourseResult execute(GetCourseQuery query) {
        var course = courseRepository.findById(query.courseId())
                .orElseThrow(() -> new CourseNotFoundException(query.courseId()));

        return CourseResult.from(course);
    }
}
