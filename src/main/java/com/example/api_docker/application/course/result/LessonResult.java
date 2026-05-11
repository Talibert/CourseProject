package com.example.api_docker.application.course.result;

import com.example.api_docker.domain.course.Lesson;

import java.util.UUID;

public record LessonResult(
        UUID lessonId,
        String title,
        int order,
        int durationMinutes
) {
    public static LessonResult from(Lesson lesson) {
        return new LessonResult(
                lesson.getId().value(),
                lesson.getTitle(),
                lesson.getOrder(),
                lesson.getDurationMinutes()
        );
    }
}
