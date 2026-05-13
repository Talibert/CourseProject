package com.example.api_docker.domain.course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    void save(Course course);
    Optional<Course> findById(CourseId id);
    List<Course> findAll();
}
