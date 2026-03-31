package com.example.api_docker.domain.course.exception;

import com.example.api_docker.domain.course.LessonId;

public class LessonNotPartOfCourseException extends RuntimeException {
    public LessonNotPartOfCourseException(LessonId lessonId) {
    }
}
